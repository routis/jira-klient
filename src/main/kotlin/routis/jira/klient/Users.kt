package routis.jira.klient

import arrow.core.Option
import com.atlassian.jira.rest.client.api.domain.User
import com.atlassian.jira.rest.client.api.domain.input.UserInput
import java.net.URI

/**
 * @see com.atlassian.jira.rest.client.api.UserRestClient
 */
@Suppress("unused")
interface Users<F> : PromiseSupport<F> {

    fun getUser(username: String): JiraKleisli<F, Option<User>> =
        withClientLookup(Ctx::getUserClient) { getUser(username) }

    fun getUser(userUri: URI): JiraKleisli<F, Option<User>> = withClientLookup(Ctx::getUserClient) { getUser(userUri) }

    fun createUser(userInput: UserInput): JiraKleisli<F, User> = withClient(Ctx::getUserClient) { createUser(userInput) }

    fun updateUser(userUri: URI, userInput: UserInput): JiraKleisli<F, User> =
        withClient(Ctx::getUserClient) { updateUser(userUri, userInput) }

    fun removeUser(userUri: URI): JiraKleisli<F, Void> =
        withClient(Ctx::getUserClient) { removeUser(userUri) }

    fun findUsers(
        username: String, startAt: Int = 0, maxResults: Int = 50,
        includeActive: Boolean = true, includeInactive: Boolean = false
    ): JiraKleisli<F, List<User>> = withClient(Ctx::getUserClient) {
        findUsers(username, startAt, maxResults, includeActive, includeInactive)
            .map { us -> us.toList() }
    }
}