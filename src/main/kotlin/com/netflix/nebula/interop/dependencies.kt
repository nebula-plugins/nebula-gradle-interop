package com.netflix.nebula.interop

import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedDependencyResult

/**
 * The selected {@link ComponentIdentifier} for a given {@link ResolvedDependencyResult}.
 */
val ResolvedDependencyResult.selectedId: ComponentIdentifier
    get() = selected.id

/**
 * The selected {@link ModuleVersionIdentifier} for a given {@link ResolvedDependencyResult}.
 */
val ResolvedDependencyResult.selectedModuleVersion: ModuleVersionIdentifier
    get() = selected?.moduleVersion ?: throw IllegalStateException("A ModuleVersionIdentifier is not available for $selected")

/**
 * The selected {@link ModuleIdentifier} for a given {@link ResolvedDependencyResult}.
 */
val ResolvedDependencyResult.selectedModule: ModuleIdentifier
    get() = selectedModuleVersion.module

/**
 * The selected version for a given {@link ResolvedDependencyResult}.
 */
val ResolvedDependencyResult.selectedVersion: String
    get() = selectedModuleVersion.version
