package routis.jira.algebra.client

import arrow.core.Option
import com.atlassian.jira.rest.client.api.SearchRestClient
import com.atlassian.jira.rest.client.api.domain.Filter
import com.atlassian.jira.rest.client.api.domain.SearchResult
import io.atlassian.util.concurrent.Promise
import routis.jira.algebra.Ctx
import routis.jira.algebra.PromiseSupport
import routis.jira.algebra.WithJira
import java.net.URI

/**
 * @see SearchRestClient
 */
interface Searches<F> : PromiseSupport<F> {

    private fun <A> withSearchClient(f: SearchRestClient.() -> Promise<A>): WithJira<F, A> =
        withClient(Ctx::getSearchClient, f)

    private fun <A> lookupWithSearchClient(f: SearchRestClient.() -> Promise<A>): WithJira<F, Option<A>> =
        lookupWithClient(Ctx::getSearchClient, f)

    /**
     * Performs a JQL search and returns issues matching the query. The first startAt issues will be skipped and SearchResult will
     * contain at most maxResults issues. List of issue fields which should be included in the result may be specified.
     *
     * @param jql        a valid JQL query (will be properly encoded by JIRA client). Restricted JQL characters (like '/') must
     * be properly escaped. All issues matches to the null or empty JQL.
     * @param maxResults maximum results for this search. When null is given, the default maxResults configured in JIRA is
     * used (usually 50).
     * @param startAt    starting index (0-based) defining how many issues should be skipped in the results. For example for
     * startAt=5 and maxResults=3 the results will include matching issues with index 5, 6 and 7.
     * For startAt = 0 and maxResults=3 the issues returned are from position 0, 1 and 2.
     * When null is given, the default startAt is used (0).
     * @param fields     set of fields which should be retrieved. You can specify *all for all fields
     * or *navigable (which is the default value, used when null is given) which will cause to include only
     * navigable fields in the result. To ignore the specific field you can use "-" before the field's name.
     * Note that the following fields: summary, issuetype, created, updated, project and status are
     * required. These fields are included in *all and *navigable.
     * @return issues matching given JQL query
     */
    fun searchJql(
        jql: String,
        maxResults: Int = 50,
        startAt: Int = 0,
        fields: Set<String> = emptySet()
    ): WithJira<F, SearchResult> = withSearchClient { searchJql(jql, maxResults, startAt, fields) }

    /**
     * Retrieves list of your favourite filters.
     *
     * @return list of your favourite filters
     * @since 2.0 client, 5.0 server
     */
    fun getFavouriteFilters(): WithJira<F, List<Filter>> = withSearchClient {
        favouriteFilters.map { fs -> fs?.toList() ?: emptyList() }
    }

    /**
     * Retrieves filter for given URI.
     *
     * @param filterUri URI to filter resource (usually get from `self` attribute describing component elsewhere)
     * @return filter
     * @since 2.0 client, 5.0 server
     */
    fun getFilter(filterUri: URI): WithJira<F, Option<Filter>> = lookupWithSearchClient {
        getFilter(filterUri)
    }

    /**
     * Retrieves filter for given id.
     *
     * @param id ID of the filter
     * @return filter
     * @since 2.0 client, 5.0 server
     */
    fun getFilter(id: Long): WithJira<F, Option<Filter>> =
        lookupWithSearchClient { getFilter(id) }


}