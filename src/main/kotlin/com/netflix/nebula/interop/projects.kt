package com.netflix.nebula.interop

import org.gradle.api.Project

/**
 * Execute an action either on {@link Project#afterEvaluate} or immediately, if the project has already been evaluated.
 */
inline fun Project.onExecute(crossinline action: () -> Unit) {
    if (state.executed) {
        action()
    } else {
        afterEvaluate { action() }
    }
}
