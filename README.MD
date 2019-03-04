# JiraKlient

This is a Kotlin-based asynchronous REST client for JIRA.
In particular, it wraps the functionality of [Atlassian's JIRA Rest client](https://bitbucket.org/atlassian/jira-rest-java-client/src/master/) 
into Kotlin using the [Arrow-kt](https://arrow-kt.io) library

## Design
Atlassian's JIRA Rest client is based on their own implementation of
a [Promise](https://bitbucket.org/atlassian/atlassian-util-concurrent/src/master/src/main/java/io/atlassian/util/concurrent/Promise.java). That is, all methods return values nested into Promise.

Besides its own capabilities, Promise implements Future, thus, the Atlassian's client can be used 
rather easily with reactive libraries like RxJava and Project Reactor.

### Goals

With **JiraKlient** we wanted to achieve two goals:
* Provide a functional client, with compose-able methods.
* Provide a tagless-final implementation, moving away of Atlassian's Promise

To achieve these we leverage the powers of [Kleisli](https://arrow-kt.io/docs/arrow/data/kleisli/).

### Main classes

* [JiraKlient](src/main/kotlin/routis/jira/klient/JiraKlient.kt): Client's algebra
* [JiraKlientForMonoK](src/main/kotlin/routis/jira/klient/instances/JiraKlientForMonoK.kt): Implementation for [Project Reactive's Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)
* [JiraKlientForSingleK](src/main/kotlin/routis/jira/klient/instances/JiraKlientForSingleK.kt): Implementation for [Rx2 Single](http://reactivex.io/documentation/single.html)
* [JiraKlientForIO](src/main/kotlin/routis/jira/klient/instances/JiraKlientForIO.kt): Implementation for [Arrow's IO](https://arrow-kt.io/docs/effects/io/)

## Why use it?
For starters, **JiraKlient** provides a tagless algebra. So, applications can use this abstraction instead of
using Atlassian's Promise.

Most importantly, though, **JiraKlient** uses the  Kleisli functional data type. This means that applications "describe"
compose-able uses of the underlying Atlassian client, without actually calling it, until is
required.

## Examples

### Lookup for an Issue and, if found, get its transitions

The following code creates a kleisli that returns an view of the Issue
that contains the data of the issue and its transitions.

```kotlin

data class IssueView(val issue: Issue, val transitions: List<Transition>)
/**
 * Conditional, reads.
 * If there is a issue, then a call for transitions will be placed
 */
fun <F> JiraKlient<F>.getIssueView(issueKey: String): JiraKleisli<F, Option<IssueView>> =
    OPTION_T_JIRA_KLEISLI.binding {

        val issue: Issue = issues.getIssue(issueKey).asOptionT().bind()
        val ts: List<Transition> = issues.getTransitions(issue).asSomeT().bind()

        IssueView(issue, ts)

    }.fix().value().fix()
```

To actually run the above code two things need to be provided
* An instance of the underlying Atlassian's JIRA client  
* An instance of the JiraKlient<F>, actually choosing effect F (Mono, Single or IO)

```kotlin
fun main() {
    
    val jiraClient = TODO("Create the JIRA client")
    
    val issueView : IO<IssueView> = JiraKlientForIO
        .getIssueView("SOME_ISSUE_KEY")
        .run(jiraClient).fix()
    
    issueView
        .unsafeRunSync()
        .also{ println(it) }
    
    client.close()
}
```
