package routis.jira.klient.instances

import arrow.Kind
import arrow.effects.ForSingleK
import arrow.effects.SingleK
import arrow.effects.k
import arrow.effects.singlek.monad.monad
import arrow.typeclasses.Monad
import io.atlassian.util.concurrent.Promise
import io.atlassian.util.concurrent.Promises
import io.reactivex.Single
import routis.jira.klient.JiraKlient

/**
 * Implementation of [JiraKlient] using [Single] monad
 */
object JiraKlientForSingleK : JiraKlient<ForSingleK> {

    override val ME: Monad<ForSingleK>
        get() = SingleK.monad()

    override fun <A> asKind(p: Promise<A>): Kind<ForSingleK, A> =
        Promises.toCompletableFuture(p).let { Single.fromFuture(it) }.k()

}