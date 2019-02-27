package routis.jira.algebra

import arrow.Kind
import arrow.core.*
import arrow.data.Kleisli
import arrow.syntax.function.pipe
import arrow.typeclasses.MonadError
import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.RestClientException
import io.atlassian.util.concurrent.Promise
import io.atlassian.util.concurrent.Promises

typealias Ctx = JiraRestClient
typealias WithJira<F, A> = Kleisli<F, Ctx, A>


interface PromiseSupport<F> {

    @Suppress("PropertyName")
    val ME: MonadError<F, Throwable>

    fun <A> asKind(p: Promise<A>): Kind<F, A>

    fun <C, A> withClient(getter: (Ctx) -> C, f: C.() -> Promise<A>): WithJira<F, A> =
        WithJira { client ->
            with(getter(client)) { f().pipe(::asKind) }
        }

    fun <C, A> withClientLookup(getter: (Ctx) -> C, f: C.() -> Promise<A>): WithJira<F, Option<A>> =
        WithJira { client ->
            with(getter(client)) {
                f().recover404WithNone().pipe(::asKind)
            }
        }

}

private fun <A> Promise<A>.recover404WithNone(): Promise<Option<A>> =
    foldToEither().flatMap { either ->
        either.fold(
            ifRight = { Promises.promise(it.some()) },
            ifLeft = {
                when (it) {
                    is EntityNotFound -> Promises.promise(none())
                    is OtherError -> Promises.rejected(it.t)
                }
            })
    }

private fun <A> Promise<A>.foldToEither(): Promise<Either<JiraOpsError, A>> =
    fold({ of(it).left() }, { it.right() })


private sealed class JiraOpsError
private object EntityNotFound : JiraOpsError()
private data class OtherError(val t: Throwable) : JiraOpsError()

private fun of(t: Throwable): JiraOpsError {

    fun isHttpError(httpErrorCode: Int) =
        t is RestClientException && t.statusCode?.or(200) == httpErrorCode

    return if (isHttpError(404)) EntityNotFound else OtherError(t)
}

