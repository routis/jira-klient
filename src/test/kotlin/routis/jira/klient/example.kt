package routis.jira.klient

import arrow.core.getOrElse
import arrow.effects.fix
import routis.jira.klient.instances.JiraKlientForMonoK

fun main(args: Array<String>) {
    val url = args[0]
    val username = args[1]
    val password = args[2]
    val issueKey = "foo"


    val client = jiraClient(url, username, password).getOrElse { throw it }
    val program = Program(JiraKlientForMonoK)
    // Nothing happened, so far.

    // Run & block for result
    val result = program.run(client, username, issueKey).fix()

    result.mono.log().block()

    client.close()
}

