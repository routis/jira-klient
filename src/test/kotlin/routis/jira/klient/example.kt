package routis.jira.klient

import arrow.Kind
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.getOrElse
import arrow.effects.ForSingleK
import arrow.effects.fix
import com.atlassian.jira.rest.client.api.JiraRestClient
import routis.jira.klient.instances.JiraKlientForSingleK

fun main(args: Array<String>) {
    val url = args[0]
    val username = args[1]
    val password = args[2]
    val issueKey = "foo"


    val client = jiraClient(url, username, password).getOrElse { throw it }
    val program: Program<ForSingleK> = Program(JiraKlientForSingleK)
    // Nothing happened, so far.

    // Run & block for result
    program.run(client, username, issueKey).fix()
        .single
        .blockingGet()
        .also { print(it) }

    client.close()
}


class Program<F>(private val klient: JiraKlient<F>) {

    fun run(
        client: JiraRestClient,
        username: String,
        issueKey: String
    ): Kind<F, Tuple2<Option<UserView>, Option<IssueView>>> {
        val operation = klient.getUserViewAndIssueView(username, issueKey)
        return operation.run(client)
    }
}



