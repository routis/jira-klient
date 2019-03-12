package routis.jira.klient

import arrow.core.getOrElse
import arrow.effects.reactor.fix
import routis.jira.klient.instances.JiraKlientForMonoK

fun main(args: Array<String>) {
    val url = args[0]
    val username = args[1]
    val password = args[2]
    val issueKey = "foo"

    val program = Program(JiraKlientForMonoK)
    val client = jiraClient(url, username, password).getOrElse { throw it }
    val operation = program.getUserViewAndIssueView(client, username, issueKey).fix()
    operation.mono.log().block()
    client.close()
}

