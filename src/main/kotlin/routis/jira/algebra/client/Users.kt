package routis.jira.algebra.client

import arrow.core.Option
import com.atlassian.jira.rest.client.api.UserRestClient
import com.atlassian.jira.rest.client.api.domain.User
import com.atlassian.jira.rest.client.api.domain.input.UserInput
import io.atlassian.util.concurrent.Promise
import routis.jira.algebra.Ctx
import routis.jira.algebra.PromiseSupport
import routis.jira.algebra.WithJira
import java.net.URI

/**
 * @see com.atlassian.jira.rest.client.api.UserRestClient
 */
@Suppress("unused")
interface Users<F> : PromiseSupport<F> {


    private fun <A> withUserClient(f: UserRestClient.() -> Promise<A>): WithJira<F, A> =
        withClient(Ctx::getUserClient, f)

    private fun <A> lookupWithUserClient(f: UserRestClient.() -> Promise<A>): WithJira<F, Option<A>> =
        lookupWithClient(Ctx::getUserClient, f)

    /**
     * Retrieves detailed information about selected user.
     * Try to use [.getUser] instead as that method is more RESTful (well connected)
     *
     * @param username JIRA username/login
     * @return complete information about given user
     *
     */
    fun getUser(username: String): WithJira<F, Option<User>> = lookupWithUserClient { getUser(username) }

    /**
     * Retrieves detailed information about selected user.
     * This method is preferred over [.getUser] as com.atlassian.jira.rest.it's more RESTful (well connected)
     *
     * @param userUri URI of user resource
     * @return complete information about given user
     *
     */
    fun getUser(userUri: URI): WithJira<F, Option<User>> = lookupWithUserClient { getUser(userUri) }

    /**
     * Create user. By default created user will not be notified with email.
     * If password field is not set then password will be randomly generated.
     *
     * @param userInput UserInput with data to update
     * @return complete information about selected user
     *
     *
     * @since v5.1.0
     */
    fun createUser(userInput: UserInput): WithJira<F, User> = withUserClient { createUser(userInput) }

    /**
     * Modify user. The "value" fields present will override the existing value.
     * Fields skipped in request will not be changed.
     *
     * @param userUri   URI to selected user resource
     * @param userInput UserInput with data to update
     * @return complete information about selected user
     *
     *
     * @since v5.1.0
     */
    fun updateUser(userUri: URI, userInput: UserInput): WithJira<F, User> =
        withUserClient { updateUser(userUri, userInput) }

    /**
     * Removes user.
     *
     * @param userUri URI to selected user resource
     * @return Void
     *
     *
     * @since v5.1.0
     */
    fun removeUser(userUri: URI): WithJira<F, Void> =
        withUserClient { removeUser(userUri) }


    /**
     * Returns a list of users that match the search string.
     * This resource cannot be accessed anonymously.
     *
     * @param username        A query string used to search username, name or e-mail address
     * @param startAt         The index of the first user to return (0-based)
     * @param maxResults      The maximum number of users to return (defaults to 50). The maximum allowed value is 1000.
     * If you specify a value that is higher than this number, your search results will be truncated.
     * @param includeActive   If true, then active users are included in the results (default true)
     * @param includeInactive If true, then inactive users are included in the results (default false)
     * @return list of users that match the search string
     *
     *
     * @since v5.1.0
     */
    fun findUsers(
        username: String, startAt: Int = 0, maxResults: Int = 50,
        includeActive: Boolean = true, includeInactive: Boolean = false
    ): WithJira<F, List<User>> = withUserClient {
        findUsers(username, startAt, maxResults, includeActive, includeInactive)
            .map { us -> us.toList() }
    }
}