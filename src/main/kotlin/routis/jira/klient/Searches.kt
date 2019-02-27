package routis.jira.klient

import arrow.core.Option
import com.atlassian.jira.rest.client.api.SearchRestClient
import com.atlassian.jira.rest.client.api.domain.Filter
import com.atlassian.jira.rest.client.api.domain.SearchResult
import java.net.URI

/**
 * @see SearchRestClient
 */
interface Searches<F> : PromiseSupport<F> {

    fun searchJql(
        jql: String,
        maxResults: Int = 50,
        startAt: Int = 0,
        fields: Set<String> = emptySet()
    ): JiraReaderT<F, SearchResult> =
        withClient(Ctx::getSearchClient) { searchJql(jql, maxResults, startAt, fields) }


    fun getFavouriteFilters(): JiraReaderT<F, List<Filter>> = withClient(Ctx::getSearchClient) {
        favouriteFilters.map { fs -> fs?.toList() ?: emptyList() }
    }

    fun getFilter(filterUri: URI): JiraReaderT<F, Option<Filter>> =
        withClientLookup(Ctx::getSearchClient) { getFilter(filterUri) }

    fun getFilter(id: Long): JiraReaderT<F, Option<Filter>> =
        withClientLookup(Ctx::getSearchClient) { getFilter(id) }
}