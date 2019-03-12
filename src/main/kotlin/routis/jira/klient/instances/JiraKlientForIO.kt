package routis.jira.klient.instances

import arrow.Kind
import arrow.core.Either
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.extensions.io.async.async

import arrow.effects.typeclasses.Async
import io.atlassian.util.concurrent.Promise
import routis.jira.klient.JiraKlient

@Suppress("unused")
object JiraKlientForIO : JiraKlient<ForIO> {

    override val ME: Async<ForIO>
        get() = IO.async()

    override fun <A> asKind(p: Promise<A>): Kind<ForIO, A> =
        IO.async { _, cb ->
            p.then(object : Promise.TryConsumer<A> {
                override fun accept(t: A): Unit = cb(Either.right(t))
                override fun fail(t: Throwable) = cb(Either.left(t))
            })
        }
}