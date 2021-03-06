package routis.jira.klient

import arrow.core.Option
import com.atlassian.jira.rest.client.api.domain.Permissions
import com.atlassian.jira.rest.client.api.domain.input.MyPermissionsInput

/**
 * Wrapper of [com.atlassian.jira.rest.client.api.MyPermissionsRestClient]
 */
interface MyPermissions<F> : PromiseSupport<F> {

    fun getMyPermissions(permissionInput: Option<MyPermissionsInput>): JiraKleisli<F, Permissions> =
        withClient(Ctx::getMyPermissionsRestClient) { getMyPermissions(permissionInput.orNull()) }
}