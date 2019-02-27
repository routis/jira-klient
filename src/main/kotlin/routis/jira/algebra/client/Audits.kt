package routis.jira.algebra.client

import com.atlassian.jira.rest.client.api.domain.AuditRecordsData
import com.atlassian.jira.rest.client.api.domain.input.AuditRecordSearchInput
import routis.jira.algebra.Ctx
import routis.jira.algebra.PromiseSupport
import routis.jira.algebra.WithJira

/**
 * @see com.atlassian.jira.rest.client.api.AuditRestClient
 */
interface Audits<F> : PromiseSupport<F> {


    fun getAuditRecords(input: AuditRecordSearchInput): WithJira<F, AuditRecordsData> =
            withClient(Ctx::getAuditRestClient){
                getAuditRecords(input)
            }


}