package routis.jira.klient

import arrow.Kind
import arrow.core.*
import arrow.data.Kleisli
import arrow.data.KleisliPartialOf
import arrow.data.OptionT
import arrow.data.OptionTPartialOf
import arrow.instances.kleisli.monad.monad
import arrow.instances.optiont.monad.monad
import arrow.syntax.function.pipe
import arrow.typeclasses.Monad
import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.RestClientException
import io.atlassian.util.concurrent.Promise
import io.atlassian.util.concurrent.Promises

typealias Ctx = JiraRestClient
typealias JiraKleisli<F, A> = Kleisli<F, Ctx, A>
typealias OptionTJiraKleisli<F, A> = OptionT<KleisliPartialOf<F, Ctx>, A>

/**
 * [JiraRestClient] is an asynchronous REST client that is build around the [Promise] class.
 * All the methods of the client are returning results wrapped into [Promise] containers.
 *
 * This class contains methods for lifting a [Promise] into another [monad effect][ME]
 *
 *
 * [ME] The monad effect into which the [Promise] (returned from [JiraRestClient]) is
 * being lifted to
 */
@Suppress("PropertyName")
interface PromiseSupport<F> {

    /**
     * The monad effect into which the [Promise] (returned from [JiraRestClient]) is
     * being lifted to
     */
    val ME: Monad<F>

    val JIRA_KLEISLI: Monad<KleisliPartialOf<F, Ctx>>
        get() = Kleisli.monad(ME)

    val OPTION_T_JIRA_KLEISLI: Monad<OptionTPartialOf<KleisliPartialOf<F, Ctx>>>
        get() = OptionT.monad(JIRA_KLEISLI)

    /**
     * Transforms a [promise][p] into a kind of [F]
     */
    fun <A> asKind(p: Promise<A>): Kind<F, A>

    /**
     * Creates a kleisli (reader) that will :
     *
     * [Gets][get] the client [C] from the [context][C],
     * executes the given [block][doWith] and transforms the resulting [Promise]
     * into the defined monad[ME]
     */
    fun <C, A> withClient(get: (Ctx) -> C, doWith: C.() -> Promise<A>): JiraKleisli<F, A> =
        JiraKleisli {
            get(it).doWith().pipe(::asKind)
        }

    /**
     * Similar to [withClient] but produces a kleisli of an [Option] of [A]
     *
     * [JiraRestClient] throws an HTTP404 error when looking up a specific entity [A]
     * that doesn't exit. So, this method 'recovers a HTTP404' to [None]
     */
    fun <C, A> withClientLookup(get: (Ctx) -> C, doWith: C.() -> Promise<A>): JiraKleisli<F, Option<A>> =
        JiraKleisli {
            get(it).doWith().recover404WithNone().pipe(::asKind)
        }

    fun <A> JiraKleisli<F, A>.asSomeT(): OptionTJiraKleisli<F, A> = asSomeT(ME)
}


/**
 *
 */
private fun <A> Promise<A>.recover404WithNone(): Promise<Option<A>> {

    fun Throwable.isHttpError(httpErrorCode: Int) =
        this is RestClientException && statusCode?.or(200) == httpErrorCode

    /**
     * Transforms a rejected (failed) promise into a (accepted) Promise  that contains
     * an [Either]
     */
    fun foldToEither(): Promise<Either<Throwable, A>> = fold({ it.left() }, { it.right() })

    return foldToEither().flatMap { either ->
        either.fold(
            ifRight = { Promises.promise(it.some()) },
            ifLeft = {
                if (it.isHttpError(404)) Promises.promise(none())
                else Promises.rejected(it)
            })
    }
}