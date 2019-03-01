package routis.jira.klient.instances

import arrow.Kind
import arrow.effects.ForMonoK
import arrow.effects.MonoK
import arrow.effects.k
import arrow.effects.monok.monad.monad
import arrow.typeclasses.Monad
import io.atlassian.util.concurrent.Promise
import io.atlassian.util.concurrent.Promises
import reactor.core.publisher.Mono
import routis.jira.klient.JiraKlient

/**
 * Implementation of [JiraKlient] using [Mono] monad
 */
@Suppress("unused")
object JiraKlientForMonoK : JiraKlient<ForMonoK> {

    override val ME: Monad<ForMonoK>
        get() = MonoK.monad()

    override fun <A> asKind(p: Promise<A>): Kind<ForMonoK, A> =
        Promises.toCompletableFuture(p).let { Mono.fromFuture(it) }.k()
}