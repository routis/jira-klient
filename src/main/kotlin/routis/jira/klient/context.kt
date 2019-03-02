package routis.jira.klient

import arrow.data.Kleisli
import arrow.data.KleisliPartialOf
import arrow.data.OptionT
import com.atlassian.jira.rest.client.api.JiraRestClient

/**
 * The dependency (or context) of [JiraKlient] to the outside world.
 * It's an alias to Atlassian's [JiraRestClient]
 */
typealias Ctx = JiraRestClient

/**
 * An alias to [Kleisli] that fixes its dependency (or context) to
 * the [JiraRestClient].
 */
typealias JiraKleisli<F, A> = Kleisli<F, Ctx, A>


typealias OptionTJiraKleisli<F, A> = OptionT<KleisliPartialOf<F, Ctx>, A>