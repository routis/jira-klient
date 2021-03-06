package routis.jira.klient

import arrow.core.Option
import com.atlassian.jira.rest.client.api.domain.BasicProject
import com.atlassian.jira.rest.client.api.domain.Project
import java.net.URI

interface Projects<F> : PromiseSupport<F> {

    fun getProject(key: String): JiraKleisli<F, Option<Project>> =
        withClientLookup(Ctx::getProjectClient) { getProject(key) }

    fun getProject(projectUri: URI): JiraKleisli<F, Option<Project>> =
        withClientLookup(Ctx::getProjectClient) { getProject(projectUri) }

    fun getAllProjects(): JiraKleisli<F, List<BasicProject>> = withClient(Ctx::getProjectClient) {
        allProjects.map { ps -> ps.toList() }
    }
}