package routis.jira.klient

import arrow.core.getOrElse
import arrow.effects.fix
import routis.jira.klient.instances.JiraKlientForIO

fun main(args: Array<String>) {
    val url = args[0]
    val username = args[1]
    val password = args[2]

    val program = Program(JiraKlientForIO)
    val client = jiraClient(url, username, password).getOrElse { throw it }
    val operation = program.getRequiredIssues(client, listOf("A", "B")).fix()

    operation.unsafeRunSync().also { print(it) }

    client.close()
}