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

    fun createIssue(issue: IssueInput): JiraReaderT<F, BasicIssue> =
        withClient(Ctx::getIssueClient) { createIssue(issue) }

    fun updateIssue(issueKey: String, issue: IssueInput): JiraReaderT<F, Void> =
        withClient(Ctx::getIssueClient) { updateIssue(issueKey, issue) }

    fun getCreateIssueMetadata(options: GetCreateIssueMetadataOptions): JiraReaderT<F, List<CimProject>> =
        withClient(Ctx::getIssueClient) {
            getCreateIssueMetadata(options).map { ps -> ps?.toList() ?: emptyList() }
        }

    fun createIssues(issues: List<IssueInput>): JiraReaderT<F, BulkOperationResult<BasicIssue>> =
        withClient(Ctx::getIssueClient) { createIssues(issues) }

    fun getIssue(issueKey: String): JiraReaderT<F, Option<Issue>> =
        withClientLookup(Ctx::getIssueClient) { getIssue(issueKey) }

    fun getIssue(issueKey: String, expand: Iterable<IssueRestClient.Expandos>): JiraReaderT<F, Option<Issue>> =
        withClientLookup(Ctx::getIssueClient) { getIssue(issueKey, expand) }

    fun deleteIssue(issueKey: String, deleteSubtasks: Boolean): JiraReaderT<F, Void> =
        withClient(Ctx::getIssueClient) { deleteIssue(issueKey, deleteSubtasks) }

    fun getWatchers(watchersUri: URI): JiraReaderT<F, Watchers> =
        withClient(Ctx::getIssueClient) { getWatchers(watchersUri) }

    fun getVotes(votesUri: URI): JiraReaderT<F, Votes> =
        withClient(Ctx::getIssueClient) { getVotes(votesUri) }

    fun getTransitions(transitionsUri: URI): JiraReaderT<F, List<Transition>> = withClient(Ctx::getIssueClient) {
        getTransitions(transitionsUri).map { ts -> ts?.toList() ?: emptyList() }
    }

    fun getTransitions(issue: Issue): JiraReaderT<F, List<Transition>> = withClient(Ctx::getIssueClient) {
        getTransitions(issue).map { ts -> ts?.toList() ?: emptyList() }
    }

    fun transition(transitionsUri: URI, transitionInput: TransitionInput): JiraReaderT<F, Void> =
        withClient(Ctx::getIssueClient) { transition(transitionsUri, transitionInput) }

    fun transition(issue: Issue, transitionInput: TransitionInput): JiraReaderT<F, Void> =
        withClient(Ctx::getIssueClient) { transition(issue, transitionInput) }

    fun vote(votesUri: URI): JiraReaderT<F, Void> =
        withClient(Ctx::getIssueClient) { vote(votesUri) }

    fun unvote(votesUri: URI): JiraReaderT<F, Void> =
        withClient(Ctx::getIssueClient) { unvote(votesUri) }

    fun watch(watchersUri: URI): JiraReaderT<F, Void> =
        withClient(Ctx::getIssueClient) { watch(watchersUri) }

    fun unwatch(watchersUri: URI): JiraReaderT<F, Void> =
        withClient(Ctx::getIssueClient) { unwatch(watchersUri) }

    fun addWatcher(watchersUri: URI, username: String): JiraReaderT<F, Void> =
        withClient(Ctx::getIssueClient) { addWatcher(watchersUri, username) }

    fun removeWatcher(watchersUri: URI, username: String): JiraReaderT<F, Void> =
        withClient(Ctx::getIssueClient) { removeWatcher(watchersUri, username) }

    fun linkIssue(linkIssuesInput: LinkIssuesInput): JiraReaderT<F, Void> =
        withClient(Ctx::getIssueClient) { linkIssue(linkIssuesInput) }

    fun addAttachment(attachmentsUri: URI, `in`: InputStream, filename: String): JiraReaderT<F, Void> =
        withClient(Ctx::getIssueClient) { addAttachment(attachmentsUri, `in`, filename) }

    fun addComment(commentsUri: URI, comment: Comment): JiraReaderT<F, Void> =
        withClient(Ctx::getIssueClient) { addComment(commentsUri, comment) }

    fun addWorklog(worklogUri: URI, worklogInput: WorklogInput): JiraReaderT<F, Void> =
        withClient(Ctx::getIssueClient) { addWorklog(worklogUri, worklogInput) }
}