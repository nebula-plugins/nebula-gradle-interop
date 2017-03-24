package com.netflix.nebula.interop

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvableDependencies

/**
 * A onResolve hook that determines is the handler has been copied, and nops if so.
 */
inline fun Configuration.onResolve(crossinline action: (ResolvableDependencies) -> Unit) {
    incoming.beforeResolve { incoming ->
        if (this.incoming == incoming) {
            action(incoming)
        }
    }
}
