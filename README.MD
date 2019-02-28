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
* [JiraKlientForMonoK](src/main/kotlin/routis/jira/klient/instances/JiraKlientForMonoK.kt): Implementation for Mono
* [JiraKlientForSingleK](src/main/kotlin/routis/jira/klient/instances/JiraKlientForSingleK.kt): Implementation for Rx2 Single

## Why use it?
For starters, **JiraKlient** provides a tagless algebra. So, applications can use this abstraction instead of
using Atlassian's Promise.

Most importantly, though, **JiraKlient** uses the  Kleisli functional data type. This means that applications "describe"
compose-able uses of the underlying Atlassian client, without actually calling it, until is
required.


