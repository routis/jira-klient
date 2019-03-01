package routis.jira.klient.instances

import arrow.Kind
import arrow.core.left
import arrow.core.right
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.instances.io.async.async
import arrow.effects.typeclasses.Async
import arrow.syntax.function.pipe
import io.atlassian.util.concurrent.Promise
import io.atlassian.util.concurrent.Promises
import routis.jira.klient.JiraKlient
import java.util.concurrent.CompletableFuture

@Suppress("unused")
object JiraKlientForIO : JiraKlient<ForIO> {

    override val ME: Async<ForIO>
        get() = IO.async()

    override fun <A> asKind(p: Promise<A>): Kind<ForIO, A> =
        Promises.toCompletableFuture(p).log() pipe { toIO2(it) }


    private fun <A> toIO(f: CompletableFuture<A>): IO<A> =
        IO.async { c, cb ->

            f.handleAsync { success, error ->
                error?.left() ?: success!!.right() pipe {cb(it)}
            }
        }

    private fun <A> toIO2(f: CompletableFuture<A>): IO<A> =
        IO.defer { IO.just(f.get()) }

    private fun <A> CompletableFuture<A>.log(): CompletableFuture<A> =
            this.thenApplyAsync { it.also { a->println("---->$a") } }
}