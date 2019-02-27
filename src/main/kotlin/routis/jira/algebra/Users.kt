package routis.jira.algebra

import arrow.core.Option
import com.atlassian.jira.rest.client.api.domain.User
import com.atlassian.jira.rest.client.api.domain.input.UserInput
import java.net.URI

/**
 * @see com.atlassian.jira.rest.client.api.UserRestClient
 */
@Suppress("unused")
interface Users<F> : PromiseSupport<F> {

    fun getUser(username: String): WithJira<F, Option<User>> =
        withClientLookup(Ctx::getUserClient) { getUser(username) }

    fun getUser(userUri: URI): WithJira<F, Option<User>> = withClientLookup(Ctx::getUserClient) { getUser(userUri) }

    fun createUser(userInput: UserInput): WithJira<F, User> = withClient(Ctx::getUserClient) { createUser(userInput) }

    fun updateUser(userUri: URI, userInput: UserInput): WithJira<F, User> =
        withClient(Ctx::getUserClient) { updateUser(userUri, userInput) }

    fun removeUser(userUri: URI): WithJira<F, Void> =
        withClient(Ctx::getUserClient) { removeUser(userUri) }

    fun findUsers(
        username: String, startAt: Int = 0, maxResults: Int = 50,
        includeActive: Boolean = true, includeInactive: Boolean = false
    ): WithJira<F, List<User>> = withClient(Ctx::getUserClient) {
        findUsers(username, startAt, maxResults, includeActive, includeInactive)
            .map { us -> us.toList() }
    }
}