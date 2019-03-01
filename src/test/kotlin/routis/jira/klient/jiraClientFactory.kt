package routis.jira.klient

import arrow.core.Try
import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import java.net.URI

fun jiraClient(url: String, username: String, password: String): Try<JiraRestClient> = Try {
    AsynchronousJiraRestClientFactory()
        .createWithBasicHttpAuthentication(URI(url), username, password)
}