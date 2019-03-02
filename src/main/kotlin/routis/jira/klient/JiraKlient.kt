package routis.jira.klient

import routis.jira.klient.JiraKlient.Companion.invoke

/**
 * Wraps the functionality of [com.atlassian.jira.rest.client.api.JiraRestClient]
 * and provides a number of kleisli-based clients.
 *
 * Use [invoke] to implement an instance by providing a [PromiseSupport]
 *
 * @see JiraKleisli This is the return type for all methods of the individual clients
 */
interface JiraKlient<F> : PromiseSupport<F> {

    val issues: Issues<F>
        get() = object : Issues<F>, PromiseSupport<F> by this {}

    val projects: Projects<F>
        get() = object : Projects<F>, PromiseSupport<F> by this {}

    val searches: Searches<F>
        get() = object : Searches<F>, PromiseSupport<F> by this {}

    val users: Users<F>
        get() = object : Users<F>, PromiseSupport<F> by this {}

    val versions: Versions<F>
        get() = object : Versions<F>, PromiseSupport<F> by this {}

    val audits: Audits<F>
        get() = object : Audits<F>, PromiseSupport<F> by this {}

    val myPermissions: MyPermissions<F>
        get() = object : MyPermissions<F>, PromiseSupport<F> by this {}

    val projectRoles: ProjectRoles<F>
        get() = object : ProjectRoles<F>, PromiseSupport<F> by this {}

    companion object {
        operator fun <F> invoke(ps: PromiseSupport<F>): JiraKlient<F> =
            object : JiraKlient<F>, PromiseSupport<F> by ps {}
    }
}