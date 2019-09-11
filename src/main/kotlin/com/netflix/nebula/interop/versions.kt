package com.netflix.nebula.interop

import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.*
import org.gradle.util.GradleVersion

private val VERSIONED_COMPARATOR = DefaultVersionComparator()
private val VERSION_COMPARATOR = VERSIONED_COMPARATOR.asVersionComparator()
private val VERSION_PARSER = VersionParser()
private val VERSION_SELECTOR_SCHEME_CLASS = DefaultVersionSelectorScheme::class.java
private val VERSION_SCHEME = if(GradleVersion.current().versionGreaterThan("5.0")) VERSION_SELECTOR_SCHEME_CLASS.getConstructor(VersionComparator::class.java).newInstance(VERSIONED_COMPARATOR) else VERSION_SELECTOR_SCHEME_CLASS.getConstructor(VersionComparator::class.java, VersionParser::class.java).newInstance(VERSIONED_COMPARATOR, VERSION_PARSER)

data class VersionWithSelector(val stringVersion: String, val version: Version = VERSION_PARSER.transform(stringVersion)) : Comparable<Version>, Version by version {
    private val selector: VersionSelector by lazy {
        VERSION_SCHEME.parseSelector(stringVersion)!!
    }

    override fun toString(): String =
            stringVersion

    fun asSelector(): VersionSelector = selector

    fun asString(): String = stringVersion

    override fun compareTo(other: Version): Int =
            VERSION_COMPARATOR.compare(this, other)
}
