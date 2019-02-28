package routis.jira.klient

import arrow.core.Option
import arrow.data.Kleisli
import arrow.data.KleisliPartialOf
import arrow.data.OptionT
import arrow.data.fix
import arrow.typeclasses.Functor

/**
 * Expresses a kleisli (reader) for an optional [A] as a [OptionT] transformer.
 *
 * This is useful when binding kleisli's that return an optional [A]
 * Function [asKleisli] provides the opposite transformation
 */
fun <F, D, A> Kleisli<F, D, Option<A>>.asOptionT(): OptionT<KleisliPartialOf<F, D>, A> = OptionT(this)

fun <F, D, A> Kleisli<F, D, A>.asSomeT(FF: Functor<F>): OptionT<KleisliPartialOf<F, D>, A> =
    OptionT.invoke(this.map(FF) { Option(it) })

fun <F, D, A> OptionT<KleisliPartialOf<F, D>, A>.asKleisli(): Kleisli<F, D, Option<A>> = value().fix()



