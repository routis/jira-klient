package routis.jira.algebra.client

import arrow.core.Option
import com.atlassian.jira.rest.client.api.ProjectRestClient
import com.atlassian.jira.rest.client.api.RestClientException
import com.atlassian.jira.rest.client.api.domain.BasicProject
import com.atlassian.jira.rest.client.api.domain.Project
import routis.jira.algebra.Ctx
import routis.jira.algebra.PromiseSupport
import routis.jira.algebra.WithJira
import java.net.URI

interface Projects<F> : PromiseSupport<F> {

    /**
     * Retrieves complete information about given project.
     *
     * @param key unique key of the project (usually 2+ characters)
     * @return complete information about given project
     * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
     */
    fun getProject(key: String): WithJira<F, Option<Project>> =
        lookupWithClient(Ctx::getProjectClient) {
            getProject(key)
        }

    /**
     * Retrieves complete information about given project.
     * Use this method rather than [ProjectRestClient.getProject]
     * wheever you can, as this method is proof for potential changes of URI scheme used for exposing various
     * resources by JIRA REST API.
     *
     * @param projectUri URI to project resource (usually get from `self` attribute describing component elsewhere
     * @return complete information about given project
     * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
     */
    fun getProject(projectUri: URI): WithJira<F, Option<Project>> =
        lookupWithClient(Ctx::getProjectClient) {
            getProject(projectUri)
        }

    /**
     * Returns all projects, which are visible for the currently logged in user. If no user is logged in, it returns the
     * list of projects that are visible when using anonymous access.
     *
     * @return projects which the currently logged user can see
     * @since com.atlassian.jira.rest.client.api: 0.2, server 4.3
     */
    fun getAllProjects(): WithJira<F, List<BasicProject>> = withClient(Ctx::getProjectClient) {
        allProjects.map { ps -> ps.toList() }
    }
}