package routis.jira.algebra

import arrow.core.Option
import com.atlassian.jira.rest.client.api.ProjectRestClient
import com.atlassian.jira.rest.client.api.RestClientException
import com.atlassian.jira.rest.client.api.domain.BasicProject
import com.atlassian.jira.rest.client.api.domain.Project
import java.net.URI

interface Projects<F> : PromiseSupport<F> {

    fun getProject(key: String): WithJira<F, Option<Project>> =
        withClientLookup(Ctx::getProjectClient) { getProject(key) }

    fun getProject(projectUri: URI): WithJira<F, Option<Project>> =
        withClientLookup(Ctx::getProjectClient) { getProject(projectUri) }

    fun getAllProjects(): WithJira<F, List<BasicProject>> = withClient(Ctx::getProjectClient) {
        allProjects.map { ps -> ps.toList() }
    }
}