package com.netflix.nebula.interop

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.gradle.api.invocation.Gradle
import org.gradle.util.GradleVersion
import org.junit.Before
import org.junit.Test

class GradleTest {
    lateinit var gradle: Gradle
    lateinit var gradleVersion: GradleVersion

    @Before
    fun setup() {
        gradle = mock<Gradle>()
        gradleVersion = mock<GradleVersion>()
        whenever(gradle.gradleVersion).thenReturn("3.4")
        whenever(gradleVersion.version).thenReturn("3.4")
    }

    @Test
    fun `version greater than`() {
        assertThat(gradle.versionGreaterThan("3.3"), equalTo(true))
    }

    @Test
    fun `version less than`() {
        assertThat(gradle.versionLessThan("3.5"), equalTo(true))
    }

    @Test
    fun `gradleVersion greater than`() {
        assertThat(gradleVersion.versionGreaterThan("3.3"), equalTo(true))
    }

    @Test
    fun `gradleVersion less than`() {
        assertThat(gradleVersion.versionLessThan("3.5"), equalTo(true))
    }

}
