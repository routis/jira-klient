package routis.jira.klient

import arrow.core.Option
import com.atlassian.jira.rest.client.api.domain.Version
import com.atlassian.jira.rest.client.api.domain.VersionRelatedIssuesCount
import com.atlassian.jira.rest.client.api.domain.input.VersionInput
import com.atlassian.jira.rest.client.api.domain.input.VersionPosition
import java.net.URI

/**
 * Wrapper of [com.atlassian.jira.rest.client.api.VersionRestClient]
 */
interface Versions<F> : PromiseSupport<F> {

    fun getVersion(versionUri: URI): JiraKleisli<F, Option<Version>> =
        withClientLookup(Ctx::getVersionRestClient) { getVersion(versionUri) }

    fun createVersion(version: VersionInput): JiraKleisli<F, Version> =
        withClient(Ctx::getVersionRestClient) { createVersion(version) }

    fun updateVersion(versionUri: URI, versionInput: VersionInput): JiraKleisli<F, Version> =
        withClient(Ctx::getVersionRestClient) { updateVersion(versionUri, versionInput) }

    fun removeVersion(
        versionUri: URI,
        moveFixIssuesToVersionUri: Option<URI>,
        moveAffectedIssuesToVersionUri: Option<URI>
    ): JiraKleisli<F, Void> = withClient(Ctx::getVersionRestClient) {
        removeVersion(
            versionUri,
            moveFixIssuesToVersionUri.orNull(),
            moveAffectedIssuesToVersionUri.orNull()
        )
    }

    fun getVersionRelatedIssuesCount(versionUri: URI): JiraKleisli<F, VersionRelatedIssuesCount> =
        withClient(Ctx::getVersionRestClient) { getVersionRelatedIssuesCount(versionUri) }

    fun getNumUnresolvedIssues(versionUri: URI): JiraKleisli<F, Int> =
        withClient(Ctx::getVersionRestClient) { getNumUnresolvedIssues(versionUri) }

    fun moveVersionAfter(versionUri: URI, afterVersionUri: URI): JiraKleisli<F, Version> =
        withClient(Ctx::getVersionRestClient) { moveVersionAfter(versionUri, afterVersionUri) }

    fun moveVersion(versionUri: URI, versionPosition: VersionPosition): JiraKleisli<F, Version> =
        withClient(Ctx::getVersionRestClient) { moveVersion(versionUri, versionPosition) }
}