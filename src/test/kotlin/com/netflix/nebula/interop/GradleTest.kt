package com.netflix.nebula.interop

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.gradle.api.invocation.Gradle
import org.junit.Before
import org.junit.Test

class GradleTest {
    lateinit var gradle: Gradle

    @Before
    fun setup() {
        gradle = mock<Gradle>()
        whenever(gradle.gradleVersion).thenReturn("3.4")
    }

    @Test
    fun `version greater than`() {
        assertThat(gradle.versionGreaterThan("3.3"), equalTo(true))
    }

    @Test
    fun `version less than`() {
        assertThat(gradle.versionLessThan("3.5"), equalTo(true))
    }
}
