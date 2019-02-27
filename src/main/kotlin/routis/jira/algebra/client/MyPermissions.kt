package routis.jira.algebra.client

import arrow.core.Option
import com.atlassian.jira.rest.client.api.domain.Permissions
import com.atlassian.jira.rest.client.api.domain.input.MyPermissionsInput
import routis.jira.algebra.Ctx
import routis.jira.algebra.PromiseSupport
import routis.jira.algebra.WithJira

/**
 * @see com.atlassian.jira.rest.client.api.MyPermissionsRestClient
 */
interface MyPermissions<F> : PromiseSupport<F> {


    /**
     * Returns permissions for current user and context defined by `permissionInput`
     *
     * @param permissionInput Permissions context ie. projectKey OR projectId OR issueKey OR issueId.
     *
     *  * When no context supplied (null) the project related permissions will return true
     * if the user has that permission in ANY project
     *  * If a project context is provided, project related permissions will return true
     * if the user has the permissions in the specified project. For permissions
     * that are determined using issue data (e.g Current Assignee), true will be returned
     * if the user meets the permission criteria in ANY issue in that project
     *  * If an issue context is provided, it will return whether or not the user
     * has each permission in that specific issue
     *
     * @return Permissions for user in the context
     */
    fun getMyPermissions(permissionInput: Option<MyPermissionsInput>): WithJira<F, Permissions> =
        withClient(Ctx::getMyPermissionsRestClient) {
            getMyPermissions(permissionInput.orNull())
        }
}