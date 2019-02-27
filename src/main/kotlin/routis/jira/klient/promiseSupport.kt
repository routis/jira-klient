package routis.jira.klient

import arrow.Kind
import arrow.core.*
import arrow.data.ReaderT
import arrow.syntax.function.pipe
import arrow.typeclasses.MonadThrow
import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.RestClientException
import io.atlassian.util.concurrent.Promise
import io.atlassian.util.concurrent.Promises

typealias Ctx = JiraRestClient
typealias JiraReaderT<F, A> = ReaderT<F, Ctx, A>


interface PromiseSupport<F> {

    @Suppress("PropertyName")
    val ME: MonadThrow<F>

    /**
     * Transforms a [promise][p] into a kind of [F]
     */
    fun <A> asKind(p: Promise<A>): Kind<F, A>

    fun <C, A> withClient(get: (Ctx) -> C, doWith: C.() -> Promise<A>): JiraReaderT<F, A> =
        JiraReaderT {
            get(it).doWith().pipe(::asKind)
        }

    fun <C, A> withClientLookup(get: (Ctx) -> C, doWith: C.() -> Promise<A>): JiraReaderT<F, Option<A>> =
        JiraReaderT {
            get(it).doWith().recover404WithNone().pipe(::asKind)
        }
}

private fun <A> Promise<A>.recover404WithNone(): Promise<Option<A>> {

    fun Throwable.isHttpError(httpErrorCode: Int) =
        this is RestClientException && statusCode?.or(200) == httpErrorCode

    fun foldToEither(): Promise<Either<Throwable, A>> =
        fold({ it.left() }, { it.right() })

    return foldToEither().flatMap { either ->
        either.fold(
            ifRight = { Promises.promise(it.some()) },
            ifLeft = {
                if (it.isHttpError(404)) Promises.promise(none())
                else Promises.rejected(it)
            })
    }
}


