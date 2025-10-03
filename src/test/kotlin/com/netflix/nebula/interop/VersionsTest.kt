package com.netflix.nebula.interop

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VersionsTest {
    @Test
    fun `selector with new version of gradle`() {
        val versionWithSelector = VersionWithSelector("1.0.0")
        val selector = versionWithSelector.asSelector()
        assertThat(selector.selector).isEqualTo("1.0.0")
    }
}