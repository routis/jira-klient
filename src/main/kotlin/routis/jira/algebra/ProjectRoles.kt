package routis.jira.algebra

import arrow.core.Option
import com.atlassian.jira.rest.client.api.domain.ProjectRole
import java.net.URI

/**
 * Wrapper of  [com.atlassian.jira.rest.client.api.ProjectRolesRestClient]
 */
interface ProjectRoles<F> : PromiseSupport<F> {

    fun getRole(uri: URI): WithJira<F, Option<ProjectRole>> =
        withClientLookup(Ctx::getProjectRolesRestClient) { getRole(uri) }

    fun getRole(projectUri: URI, roleId: Long): WithJira<F, Option<ProjectRole>> =
        withClientLookup(Ctx::getProjectRolesRestClient) { getRole(projectUri, roleId) }

    fun getRoles(projectUri: URI): WithJira<F, List<ProjectRole>> =
        withClient(Ctx::getProjectRolesRestClient) {
            getRoles(projectUri).map { rs -> rs.toList() }
        }
}