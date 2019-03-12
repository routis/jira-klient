package routis.jira.klient

import arrow.Kind
import arrow.core.Option
import arrow.core.Try
import arrow.core.Tuple2
import arrow.data.extensions.list.traverse.traverse
import arrow.data.extensions.optiont.fx.fx
import arrow.data.fix
import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.Transition
import com.atlassian.jira.rest.client.api.domain.User
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import routis.jira.klient.util.asKleisli
import routis.jira.klient.util.asOptionT
import java.net.URI

class Program<F>(private val klient: JiraKlient<F>) {

    fun getUserViewAndIssueView(
        client: JiraRestClient,
        username: String,
        issueKey: String
    ): Kind<F, Tuple2<Option<UserView>, Option<IssueView>>> {
        val operation = klient.getUserViewAndIssueView(username, issueKey)
        return operation.run(client)
    }

    fun getRequiredIssues(client: JiraRestClient, issueKeys: List<String>): Kind<F, Option<List<Issue>>> {
        return klient.getRequiredIssues(issueKeys).run(client)
    }
}


/**
 * The two operations will be evaluated individually since we use the [arrow.typeclasses.Applicative.tupled]
 * That is two independent calls will be made
 */
private fun <F> JiraKlient<F>.getUserViewAndIssueView(
    username: String,
    issueKey: String
): JiraKleisli<F, Tuple2<Option<UserView>, Option<IssueView>>> =
    JIRA_KLEISLI.tupled(
        getUserView(username),
        getIssueView(issueKey)
    ).fix()


data class UserView(val userName: String, val emailAddress: String) {
    companion object {
        fun of(u: User): UserView = UserView(u.name, u.emailAddress)
    }
}

private fun <F> JiraKlient<F>.getUserView(username: String): JiraKleisli<F, Option<UserView>> =
    users.getUser(username)
        .asOptionT()
        .map(JIRA_KLEISLI) { UserView.of(it) }
        .asKleisli()


data class IssueView(val issue: Issue, val transitions: List<Transition>)

/**
 * Conditional, reads.
 *
 * If there is a issue, then a call for transitions will be placed
 */
private fun <F> JiraKlient<F>.getIssueView(issueKey: String): JiraKleisli<F, Option<IssueView>> =
    fx(JIRA_KLEISLI) {
        val issue = issues.getIssue(issueKey).asOptionT().bind()
        val ts = issues.getTransitions(issue).asSomeT().bind()
        IssueView(issue, ts)
    }.value().fix()

/**
 * All issues should be found, or nothing is returned
 */
private fun <F> JiraKlient<F>.getRequiredIssues(issueKeys: List<String>) =
    issueKeys.traverse(OPTION_T_JIRA_KLEISLI) { issueKey ->
        issues.getIssue(issueKey).asOptionT()
    }.fix()
        .map(JIRA_KLEISLI) { it.fix() as List<Issue> }
        .asKleisli()


fun jiraClient(url: String, username: String, password: String): Try<JiraRestClient> = Try {
    AsynchronousJiraRestClientFactory()
        .createWithBasicHttpAuthentication(URI(url), username, password)
}
