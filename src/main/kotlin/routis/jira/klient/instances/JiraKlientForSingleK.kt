package routis.jira.klient.instances

import arrow.Kind
import arrow.effects.rx2.ForSingleK
import arrow.effects.rx2.SingleK
import arrow.effects.rx2.extensions.singlek.async.async
import arrow.effects.rx2.k

import arrow.effects.typeclasses.Async
import io.atlassian.util.concurrent.Promise
import io.atlassian.util.concurrent.Promises
import io.reactivex.Single
import routis.jira.klient.JiraKlient

/**
 * Implementation of [JiraKlient] using [Single] monad
 */
@Suppress("unused")
object JiraKlientForSingleK : JiraKlient<ForSingleK> {

    override val ME: Async<ForSingleK>
        get() = SingleK.async()

    override fun <A> asKind(p: Promise<A>): Kind<ForSingleK, A> =
        Promises.toCompletableFuture(p).let { Single.fromFuture(it) }.k()

}