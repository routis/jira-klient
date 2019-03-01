package routis.jira.klient

import arrow.core.Option
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptions
import com.atlassian.jira.rest.client.api.IssueRestClient
import com.atlassian.jira.rest.client.api.domain.*
import com.atlassian.jira.rest.client.api.domain.input.IssueInput
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput
import com.atlassian.jira.rest.client.api.domain.input.WorklogInput
import java.io.InputStream
import java.net.URI

/**
 * Wrapper of [com.atlassian.jira.rest.client.api.IssueRestClient]
 */
@Suppress("unused")
interface Issues<F> : PromiseSupport<F> {

    fun createIssue(issue: IssueInput): JiraKleisli<F, BasicIssue> =
        withClient(Ctx::getIssueClient) { createIssue(issue) }

    fun updateIssue(issueKey: String, issue: IssueInput): JiraKleisli<F, Void> =
        withClient(Ctx::getIssueClient) { updateIssue(issueKey, issue) }

    fun getCreateIssueMetadata(options: GetCreateIssueMetadataOptions): JiraKleisli<F, List<CimProject>> =
        withClient(Ctx::getIssueClient) {
            getCreateIssueMetadata(options).map { ps -> ps?.toList() ?: emptyList() }
        }

    fun createIssues(issues: List<IssueInput>): JiraKleisli<F, BulkOperationResult<BasicIssue>> =
        withClient(Ctx::getIssueClient) { createIssues(issues) }

    fun getIssue(issueKey: String): JiraKleisli<F, Option<Issue>> =
        withClientLookup(Ctx::getIssueClient) { getIssue(issueKey) }

    fun getIssue(issueKey: String, expand: Iterable<IssueRestClient.Expandos>): JiraKleisli<F, Option<Issue>> =
        withClientLookup(Ctx::getIssueClient) { getIssue(issueKey, expand) }

    fun deleteIssue(issueKey: String, deleteSubtasks: Boolean): JiraKleisli<F, Void> =
        withClient(Ctx::getIssueClient) { deleteIssue(issueKey, deleteSubtasks) }

    fun getWatchers(watchersUri: URI): JiraKleisli<F, Watchers> =
        withClient(Ctx::getIssueClient) { getWatchers(watchersUri) }

    fun getVotes(votesUri: URI): JiraKleisli<F, Votes> =
        withClient(Ctx::getIssueClient) { getVotes(votesUri) }

    fun getTransitions(transitionsUri: URI): JiraKleisli<F, List<Transition>> = withClient(Ctx::getIssueClient) {
        getTransitions(transitionsUri).map { ts -> ts?.toList() ?: emptyList() }
    }

    fun getTransitions(issue: Issue): JiraKleisli<F, List<Transition>> = withClient(Ctx::getIssueClient) {
        getTransitions(issue).map { ts -> ts?.toList() ?: emptyList() }
    }

    fun transition(transitionsUri: URI, transitionInput: TransitionInput): JiraKleisli<F, Void> =
        withClient(Ctx::getIssueClient) { transition(transitionsUri, transitionInput) }

    fun transition(issue: Issue, transitionInput: TransitionInput): JiraKleisli<F, Void> =
        withClient(Ctx::getIssueClient) { transition(issue, transitionInput) }

    fun vote(votesUri: URI): JiraKleisli<F, Void> =
        withClient(Ctx::getIssueClient) { vote(votesUri) }

    fun unvote(votesUri: URI): JiraKleisli<F, Void> =
        withClient(Ctx::getIssueClient) { unvote(votesUri) }

    fun watch(watchersUri: URI): JiraKleisli<F, Void> =
        withClient(Ctx::getIssueClient) { watch(watchersUri) }

    fun unwatch(watchersUri: URI): JiraKleisli<F, Void> =
        withClient(Ctx::getIssueClient) { unwatch(watchersUri) }

    fun addWatcher(watchersUri: URI, username: String): JiraKleisli<F, Void> =
        withClient(Ctx::getIssueClient) { addWatcher(watchersUri, username) }

    fun removeWatcher(watchersUri: URI, username: String): JiraKleisli<F, Void> =
        withClient(Ctx::getIssueClient) { removeWatcher(watchersUri, username) }

    fun linkIssue(linkIssuesInput: LinkIssuesInput): JiraKleisli<F, Void> =
        withClient(Ctx::getIssueClient) { linkIssue(linkIssuesInput) }

    fun addAttachment(attachmentsUri: URI, `in`: InputStream, filename: String): JiraKleisli<F, Void> =
        withClient(Ctx::getIssueClient) { addAttachment(attachmentsUri, `in`, filename) }

    fun addComment(commentsUri: URI, comment: Comment): JiraKleisli<F, Void> =
        withClient(Ctx::getIssueClient) { addComment(commentsUri, comment) }

    fun addWorklog(worklogUri: URI, worklogInput: WorklogInput): JiraKleisli<F, Void> =
        withClient(Ctx::getIssueClient) { addWorklog(worklogUri, worklogInput) }
}