package routis.jira.algebra.client

import arrow.core.Option
import com.atlassian.jira.rest.client.api.RestClientException
import com.atlassian.jira.rest.client.api.domain.ProjectRole
import routis.jira.algebra.Ctx
import routis.jira.algebra.PromiseSupport
import routis.jira.algebra.WithJira
import java.net.URI

/**
 * @see com.atlassian.jira.rest.client.api.ProjectRolesRestClient
 */
interface ProjectRoles<F> : PromiseSupport<F> {
    /**
     * Retrieves a full information about the selected role.
     *
     * @param uri URI of the role to retrieve.
     * @return full information about selected role.
     */
    fun getRole(uri: URI): WithJira<F, Option<ProjectRole>> =
        lookupWithClient(Ctx::getProjectRolesRestClient) {
            getRole(uri)
        }

    /**
     * Retrieves a full information about the selected role.
     *
     * @param projectUri uri of the project of the role to retrieve.
     * @param roleId     unique role id.
     * @return full information about selected role.
     * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
     */
    fun getRole(projectUri: URI, roleId: Long): WithJira<F, Option<ProjectRole>> =
        lookupWithClient(Ctx::getProjectRolesRestClient) {
            getRole(projectUri, roleId)
        }

    /**
     * Retrieves a collection of roles in the selected project.
     *
     * @param projectUri uri of the project of the roles to retrieve.
     * @return a collection of roles in the selected project.
     * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
     */
    fun getRoles(projectUri: URI): WithJira<F, List<ProjectRole>> =
        withClient(Ctx::getProjectRolesRestClient){
            getRoles(projectUri).map { rs->rs.toList() }
        }
}