package routis.jira.klient.instances

import arrow.Kind
import arrow.effects.ForSingleK
import arrow.effects.SingleK
import arrow.effects.k
import arrow.effects.singlek.async.async
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