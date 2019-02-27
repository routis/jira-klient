package routis.jira.algebra.client

import arrow.core.Option
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptions
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder
import com.atlassian.jira.rest.client.api.IssueRestClient
import com.atlassian.jira.rest.client.api.domain.*
import com.atlassian.jira.rest.client.api.domain.input.IssueInput
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput
import com.atlassian.jira.rest.client.api.domain.input.WorklogInput
import io.atlassian.util.concurrent.Promise
import routis.jira.algebra.Ctx
import routis.jira.algebra.PromiseSupport
import routis.jira.algebra.WithJira
import java.io.InputStream
import java.net.URI

/**
 * @see IssueRestClient
 */
@Suppress("unused")
interface Issues<F> : PromiseSupport<F> {


    private fun <A> withIssueClient(f: IssueRestClient.() -> Promise<A>): WithJira<F, A> =
        withClient(Ctx::getIssueClient, f)

    private fun <A> lookupWithIssueClient(f: IssueRestClient.() -> Promise<A>): WithJira<F, Option<A>> =
        lookupWithClient(Ctx::getIssueClient, f)

    /**
     * Creates new issue.
     *
     * @param issue populated with data to create new issue
     * @return basicIssue with generated `issueKey`
     */
    fun createIssue(issue: IssueInput): WithJira<F, BasicIssue> = withIssueClient { createIssue(issue) }

    /**
     * Update an existing issue.
     *
     * @param issueKey issue key (like TST-1, or JRA-9)
     * @param issue    populated with fields to set (no other verbs) in issue
     */
    fun updateIssue(issueKey: String, issue: IssueInput): WithJira<F, Void> = withIssueClient {
        updateIssue(issueKey, issue)
    }

    /**
     * Retrieves CreateIssueMetadata with specified filters.
     *
     * @param options optional request configuration like filters and expandos. You may use [GetCreateIssueMetadataOptionsBuilder] to build them. Pass `null` if you don't want to set any option.
     * @return List of [CimProject] describing projects, issue types and fields.
     */
    fun getCreateIssueMetadata(options: GetCreateIssueMetadataOptions): WithJira<F, List<CimProject>> =
        withIssueClient {
            getCreateIssueMetadata(options).map { ps -> ps?.toList() ?: emptyList() }
        }

    /**
     * Creates new issues in batch.
     *
     * @param issues populated with data to create new issue
     * @return BulkOperationResult&lt;BasicIssues&gt; with generated `issueKey` and errors for failed issues
     */

    fun createIssues(issues: List<IssueInput>): WithJira<F, BulkOperationResult<BasicIssue>> = withIssueClient {
        createIssues(issues)
    }

    /**
     * Retrieves issue with selected issue key.
     *
     * @param issueKey issue key (like TST-1, or JRA-9)
     * @return issue with given `issueKey`
     */
    fun getIssue(issueKey: String): WithJira<F, Option<Issue>> = lookupWithIssueClient {
        getIssue(issueKey)
    }


    /**
     * Retrieves issue with selected issue key, with specified additional expandos.
     *
     * @param issueKey issue key (like TST-1, or JRA-9)
     * @param expand   additional expands. Currently CHANGELOG is the only supported expand that is not expanded by default.
     * @return issue with given `issueKey`
     */
    fun getIssue(issueKey: String, expand: Iterable<IssueRestClient.Expandos>): WithJira<F, Option<Issue>> =
        lookupWithIssueClient {
            getIssue(issueKey, expand)
        }

    /**
     * Deletes issue with given issueKey. You can set `deleteSubtasks` to delete issue with subtasks. If issue have
     * subtasks and `deleteSubtasks` is set to false, then issue won't be deleted.
     *
     * @param issueKey       issue key (like TST-1, or JRA-9)
     * @param deleteSubtasks Determines if subtask of issue should be also deleted. If false, and issue has subtasks, then it
     * won't be deleted.
     * @return Void
     */
    fun deleteIssue(issueKey: String, deleteSubtasks: Boolean): WithJira<F, Void> = withIssueClient {
        deleteIssue(issueKey, deleteSubtasks)
    }

    /**
     * Retrieves complete information (if the caller has permission) about watchers for selected issue.
     *
     * @param watchersUri URI of watchers resource for selected issue. Usually obtained by calling `Issue.getWatchers().getSelf()`
     * @return detailed information about watchers watching selected issue.
     */
    fun getWatchers(watchersUri: URI): WithJira<F, Watchers> = withIssueClient {
        getWatchers(watchersUri)
    }

    /**
     * Retrieves complete information (if the caller has permission) about voters for selected issue.
     *
     * @param votesUri URI of voters resource for selected issue. Usually obtained by calling `Issue.getVotesUri()`
     * @return detailed information about voters of selected issue
     */
    fun getVotes(votesUri: URI): WithJira<F, Votes> = withIssueClient { getVotes(votesUri) }


    /**
     * Retrieves complete information (if the caller has permission) about transitions available for the selected issue in its current state.
     *
     * @param transitionsUri URI of transitions resource of selected issue. Usually obtained by calling `Issue.getTransitionsUri()`
     * @return transitions about transitions available for the selected issue in its current state.
     */
    fun getTransitions(transitionsUri: URI): WithJira<F, List<Transition>> = withIssueClient {
        getTransitions(transitionsUri).map { ts -> ts?.toList() ?: emptyList() }
    }

    /**
     * Retrieves complete information (if the caller has permission) about transitions available for the selected issue in its current state.
     * @param issue issue
     * @return transitions about transitions available for the selected issue in its current state.
     */
    fun getTransitions(issue: Issue): WithJira<F, List<Transition>> = withIssueClient {
        getTransitions(issue).map { ts -> ts?.toList() ?: emptyList() }
    }

    /**
     * Performs selected transition on selected issue.
     *
     * @param transitionsUri  URI of transitions resource of selected issue. Usually obtained by calling `Issue.getTransitionsUri()`
     * @param transitionInput data for this transition (fields modified, the comment, etc.)
     */
    fun transition(transitionsUri: URI, transitionInput: TransitionInput): WithJira<F, Void> = withIssueClient {
        transition(transitionsUri, transitionInput)
    }

    /**
     * Performs selected transition on selected issue.
     *
     * @param issue           selected issue
     * @param transitionInput data for this transition (fields modified, the comment, etc.)
     */
    fun transition(issue: Issue, transitionInput: TransitionInput): WithJira<F, Void> = withIssueClient {
        transition(issue, transitionInput)
    }

    /**
     * Casts your vote on the selected issue. Casting a vote on already votes issue by the caller, causes the exception.
     *
     * @param votesUri URI of votes resource for selected issue. Usually obtained by calling `Issue.getVotesUri()`
     *
     */
    fun vote(votesUri: URI): WithJira<F, Void> = withIssueClient { vote(votesUri) }

    /**
     * Removes your vote from the selected issue. Removing a vote from the issue without your vote causes the exception.
     *
     * @param votesUri URI of votes resource for selected issue. Usually obtained by calling `Issue.getVotesUri()`
     *
     */
    fun unvote(votesUri: URI): WithJira<F, Void> = withIssueClient {
        unvote(votesUri)
    }

    /**
     * Starts watching selected issue
     *
     * @param watchersUri URI of watchers resource for selected issue. Usually obtained by calling `Issue.getWatchers().getSelf()`
     *
     */
    fun watch(watchersUri: URI): WithJira<F, Void> = withIssueClient {
        watch(watchersUri)
    }

    /**
     * Stops watching selected issue
     *
     * @param watchersUri URI of watchers resource for selected issue. Usually obtained by calling `Issue.getWatchers().getSelf()`
     *
     */
    fun unwatch(watchersUri: URI): WithJira<F, Void> = withIssueClient {
        unwatch(watchersUri)
    }

    /**
     * Adds selected person as a watcher for selected issue. You need to have permissions to do that (otherwise
     * the exception is thrown).
     *
     * @param watchersUri URI of watchers resource for selected issue. Usually obtained by calling `Issue.getWatchers().getSelf()`
     * @param username    user to add as a watcher
     *
     */
    fun addWatcher(watchersUri: URI, username: String): WithJira<F, Void> = withIssueClient {
        addWatcher(watchersUri, username)
    }

    /**
     * Removes selected person from the watchers list for selected issue. You need to have permissions to do that (otherwise
     * the exception is thrown).
     *
     * @param watchersUri URI of watchers resource for selected issue. Usually obtained by calling `Issue.getWatchers().getSelf()`
     * @param username    user to remove from the watcher list
     */
    fun removeWatcher(watchersUri: URI, username: String): WithJira<F, Void> = withIssueClient {
        removeWatcher(watchersUri, username)
    }

    /**
     * Creates link between two issues and adds a comment (optional) to the source issues.
     *
     * @param linkIssuesInput details for the link and the comment (optional) to be created
     */
    fun linkIssue(linkIssuesInput: LinkIssuesInput): WithJira<F, Void> = withIssueClient {
        linkIssue(linkIssuesInput)
    }

    /**
     * Uploads attachments to JIRA (adding it to selected issue)
     *
     * @param attachmentsUri where to upload the attachment. You can get this URI by examining issue resource first
     * @param in             stream from which to read data to upload
     * @param filename       file name to use for the uploaded attachment
     */
    fun addAttachment(attachmentsUri: URI, `in`: InputStream, filename: String): WithJira<F, Void> = withIssueClient {
        addAttachment(attachmentsUri, `in`, filename)
    }

//    /**
//     * Uploads attachments to JIRA (adding it to selected issue)
//     *
//     * @param attachmentsUri where to upload the attachments. You can get this URI by examining issue resource first
//     * @param attachments    attachments to upload
//     * @since com.atlassian.jira.rest.client.api 0.2, server 4.3
//     */
//    fun addAttachments(attachmentsUri: URI, vararg attachments: AttachmentInput): WithJira<F, Void> = withClient {
//        it.addAttachments(attachmentsUri, attachments)
//    }
//
//    /**
//     * Uploads attachments to JIRA (adding it to selected issue)
//     *
//     * @param attachmentsUri where to upload the attachments. You can get this URI by examining issue resource first
//     * @param files          files to upload
//     * @since com.atlassian.jira.rest.client.api 0.2, server 4.3
//     */
//    fun addAttachments(attachmentsUri: URI, vararg files: File): WithJira<F, Void> = withClient {
//        it.addAttachments(attachmentsUri, files)
//    }

    /**
     * Adds a comment to JIRA (adding it to selected issue)
     *
     * @param commentsUri where to add comment
     * @param comment     the [Comment] to add
     */
    fun addComment(commentsUri: URI, comment: Comment): WithJira<F, Void> = withIssueClient {
        addComment(commentsUri, comment)
    }

//    /**
//     * Retrieves the content of given attachment.
//     *
//     * @param attachmentUri URI for the attachment to retrieve
//     */
//    fun getAttachment(attachmentUri: URI): WithJira<F, InputStream> = withClient {
//        it.getAttachment(attachmentUri)
//    }

    /**
     * Adds new worklog entry to issue.
     *
     * @param worklogUri   URI for worklog in issue
     * @param worklogInput worklog input object to create
     */
    fun addWorklog(worklogUri: URI, worklogInput: WorklogInput): WithJira<F, Void> = withIssueClient {
        addWorklog(worklogUri, worklogInput)
    }
}