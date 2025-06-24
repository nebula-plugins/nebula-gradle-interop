package com.netflix.nebula.interop

import groovy.lang.Closure
import org.gradle.api.Action

/**
 * Wrap a lambda function inside a groovy closure for interop with Gradle APIs that do not yet provide an action.
 */
inline fun <T> T.groovyClosure(crossinline call: () -> Unit) = object : Closure<Unit>(this) {
    @Suppress("unused")
    fun doCall() {
        call()
    }
}

/**
 * An action that provides the receiver as a extension on the argument, allowing the members of the receiver to be used
 * without qualifying with {@code it} or a named lambda argument.
 */
inline fun <U : Any> Any.action(crossinline call: U.() -> Unit) = Action<U> { call(it) }
