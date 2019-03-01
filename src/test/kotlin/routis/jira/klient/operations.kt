package routis.jira.klient

import arrow.core.Option
import arrow.core.Tuple2
import arrow.data.fix
import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.Transition
import com.atlassian.jira.rest.client.api.domain.User

/**
 * The two operations will be evaluated individually since we use the [arrow.typeclasses.Applicative.tupled]
 * That is two independent calls will be made
 *
 */
fun <F> JiraKlient<F>.getUserViewAndIssueView(
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

fun <F> JiraKlient<F>.getUserView(username: String): JiraKleisli<F, Option<UserView>> =
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
    OPTION_T_JIRA_KLEISLI.binding {

        val issue = issues.getIssue(issueKey).asOptionT().bind()
        val ts = issues.getTransitions(issue).asSomeT().bind()
        IssueView(issue, ts)

    }.fix().value().fix()