package routis.jira.algebra

import com.atlassian.jira.rest.client.api.domain.AuditRecordsData
import com.atlassian.jira.rest.client.api.domain.input.AuditRecordSearchInput

/**
 * Wrapper of [com.atlassian.jira.rest.client.api.AuditRestClient]
 */
interface Audits<F> : PromiseSupport<F> {

    fun getAuditRecords(input: AuditRecordSearchInput): WithJira<F, AuditRecordsData> =
        withClient(Ctx::getAuditRestClient) { getAuditRecords(input) }
}