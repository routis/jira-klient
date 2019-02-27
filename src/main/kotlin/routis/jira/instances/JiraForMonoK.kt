package routis.jira.instances

import arrow.Kind
import arrow.effects.ForMonoK
import arrow.effects.MonoK
import arrow.effects.k
import arrow.effects.monok.monadError.monadError
import arrow.typeclasses.MonadError
import io.atlassian.util.concurrent.Promises
import reactor.core.publisher.Mono
import routis.jira.algebra.Jira

/**
 * Implementation of [Jira] using [Mono]
 */
object JiraForMonoK : Jira<ForMonoK> {

    override val ME: MonadError<ForMonoK, Throwable> = MonoK.monadError()

    override fun <A> asKind(p: io.atlassian.util.concurrent.Promise<A>): Kind<ForMonoK, A> = p.asMono().k()

    private fun <T> io.atlassian.util.concurrent.Promise<T>.asMono(): Mono<T> =
        Promises.toCompletableFuture(this).let { Mono.fromFuture(it) }
}