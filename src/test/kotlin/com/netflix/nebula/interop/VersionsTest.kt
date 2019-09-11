package com.netflix.nebula.interop

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

class VersionsTest {
    @Test
    fun `selector with new version of gradle`() {
        val versionWithSelector = VersionWithSelector("1.0.0")
        val selector = versionWithSelector.asSelector()
        assertThat(selector.selector, equalTo("1.0.0"))
    }
}