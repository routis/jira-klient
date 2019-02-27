package routis.jira.algebra.client

import arrow.core.Option
import com.atlassian.jira.rest.client.api.domain.Version
import com.atlassian.jira.rest.client.api.domain.VersionRelatedIssuesCount
import com.atlassian.jira.rest.client.api.domain.input.VersionInput
import com.atlassian.jira.rest.client.api.domain.input.VersionPosition
import routis.jira.algebra.Ctx
import routis.jira.algebra.PromiseSupport
import routis.jira.algebra.WithJira
import java.net.URI

interface Versions<F> : PromiseSupport<F> {
    /**
     * Retrieves full information about selected project version
     *
     * @param versionUri URI of the version to retrieve. You can get it for example from Project or it can be
     * referenced from an issue.
     * @return full information about selected project version
     *
     */
    fun getVersion(versionUri: URI): WithJira<F, Option<Version>> =
        lookupWithClient(Ctx::getVersionRestClient) {
            getVersion(versionUri)
        }

    /**
     * Creates a new version (which logically belongs to a project)
     *
     * @param version details about version to create
     * @return newly created version
     *
     */
    fun createVersion(version: VersionInput): WithJira<F, Version> =
        withClient(Ctx::getVersionRestClient) {
            createVersion(version)
        }

    /**
     * Updates selected version with a new details.
     *
     * @param versionUri   full URI to the version to update
     * @param versionInput new details of the version. `null` fields will be ignored
     * @return newly updated version
     *
     */
    fun updateVersion(versionUri: URI, versionInput: VersionInput): WithJira<F, Version> =
        withClient(Ctx::getVersionRestClient) {
            updateVersion(versionUri, versionInput)
        }

    /**
     * Removes selected version optionally changing Fix Version(s) and/or Affects Version(s) fields of related issues.
     *
     * @param versionUri                     full URI to the version to remove
     * @param moveFixIssuesToVersionUri      URI of the version to which issues should have now set their Fix Version(s)
     * field instead of the just removed version. Use `null` to simply clear Fix Version(s) in all those issues
     * where the version removed was referenced.
     * @param moveAffectedIssuesToVersionUri URI of the version to which issues should have now set their Affects Version(s)
     * field instead of the just removed version. Use `null` to simply clear Affects Version(s) in all those issues
     * where the version removed was referenced.
     *
     */
    fun removeVersion(
        versionUri: URI,
        moveFixIssuesToVersionUri: Option<URI>,
        moveAffectedIssuesToVersionUri: Option<URI>
    ): WithJira<F, Void> = withClient(Ctx::getVersionRestClient) {
        removeVersion(
            versionUri,
            moveFixIssuesToVersionUri.orNull(),
            moveAffectedIssuesToVersionUri.orNull()
        )
    }

    /**
     * Retrieves basic statistics about issues which have their Fix Version(s) or Affects Version(s) field
     * pointing to given version.
     *
     * @param versionUri full URI to the version you want to get related issues count for
     * @return basic stats about issues related to given version
     *
     */
    fun getVersionRelatedIssuesCount(versionUri: URI): WithJira<F, VersionRelatedIssuesCount> =
        withClient(Ctx::getVersionRestClient) {
            getVersionRelatedIssuesCount(versionUri)
        }

    /**
     * Retrieves number of unresolved issues which have their Fix Version(s) field
     * pointing to given version.
     *
     * @param versionUri full URI to the version you want to get the number of unresolved issues for
     * @return number of unresolved issues having this version included in their Fix Version(s) field.
     *
     */
    fun getNumUnresolvedIssues(versionUri: URI): WithJira<F, Int> = withClient(Ctx::getVersionRestClient) {
        getNumUnresolvedIssues(versionUri)
    }

    /**
     * Moves selected version after another version. Ordering of versions is important on various reports and whenever
     * input version fields are rendered by JIRA.
     * If version is already immediately after the other version (defined by `afterVersionUri`) then
     * such call has no visual effect.
     *
     * @param versionUri      full URI to the version to move
     * @param afterVersionUri URI of the version to move selected version after
     * @return just moved version
     *
     */
    fun moveVersionAfter(versionUri: URI, afterVersionUri: URI): WithJira<F, Version> =
        withClient(Ctx::getVersionRestClient) {
            moveVersionAfter(versionUri, afterVersionUri)
        }

    /**
     * Moves selected version to another position.
     * If version already occupies given position (e.g. is the last version and we want to move to a later position or to the last position)
     * then such call does not change anything.
     *
     * @param versionUri      full URI to the version to move
     * @param versionPosition defines a new position of selected version
     * @return just moved version
     *
     */
    fun moveVersion(versionUri: URI, versionPosition: VersionPosition): WithJira<F, Version> =
        withClient(Ctx::getVersionRestClient) {
            moveVersion(versionUri, versionPosition)
        }
}