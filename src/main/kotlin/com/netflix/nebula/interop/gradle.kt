package com.netflix.nebula.interop

import org.gradle.api.invocation.Gradle
import org.gradle.util.GradleVersion

fun Gradle.versionGreaterThan(version: String): Boolean =
        versionCompareTo(version) > 0

fun Gradle.versionLessThan(version: String): Boolean =
        versionCompareTo(version) < 0

fun Gradle.versionCompareTo(version: String): Int {
    return GradleVersion.version(gradleVersion).compareTo(GradleVersion.version(version))
}

fun GradleVersion.versionGreaterThan(version: String): Boolean =
        versionCompareTo(version) > 0

fun GradleVersion.versionLessThan(version: String): Boolean =
        versionCompareTo(version) < 0

fun GradleVersion.versionCompareTo(version: String): Int {
    return GradleVersion.version(this.version).compareTo(GradleVersion.version(version))
}

