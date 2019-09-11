package com.netflix.nebula.interop

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.VersionParser
import org.gradle.api.invocation.Gradle
import org.junit.Before
import org.junit.Test

class VersionsTest {
    lateinit var gradle: Gradle

    @Before
    fun setup() {
        gradle = mock<Gradle>()
    }

    @Test
    fun `selector with null version of gradle`() {
        val versionWithSelector = VersionWithSelector("1.0.0")
        val selector = versionWithSelector.asSelector()
        assertThat(selector.selector, equalTo("1.0.0"))
    }

    @Test
    fun `selector with old version of gradle`() {
        whenever(gradle.gradleVersion).thenReturn("3.4")
        val versionWithSelector = VersionWithSelector("1.0.0", gradle)
        val selector = versionWithSelector.asSelector()
        assertThat(selector.selector, equalTo("1.0.0"))
    }

    @Test
    fun `selector with new version of gradle`() {
        whenever(gradle.gradleVersion).thenReturn("5.6.2")
        val versionWithSelector = VersionWithSelector("1.0.0", gradle)
        val selector = versionWithSelector.asSelector()
        assertThat(selector.selector, equalTo("1.0.0"))
    }

}