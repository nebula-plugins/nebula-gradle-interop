package com.netflix.nebula.interop

import nebula.test.dsl.BuildscriptLanguage
import nebula.test.dsl.rootProject
import nebula.test.dsl.run
import nebula.test.dsl.testProject
import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

class VersionsIntegration {
    @TempDir
    lateinit var projectDir: File

    @ParameterizedTest
    @ValueSource(strings = ["8.14.3", "9.1.0"])
    fun test(gradleVersionToUse: String) {
        val jar = File("./build/libs").listFiles()
            .filter { !it.name.contains("javadoc") }
            .filter { !it.name.contains("sources") }
            .maxByOrNull { it.lastModified() }!!
        val testProject = testProject(projectDir, BuildscriptLanguage.GROOVY) {
            rootProject {
                rawBuildScript(
                    """
buildscript {
    dependencies {
        classpath(files("${jar.absolutePath}"))
    }
}
        import com.netflix.nebula.interop.VersionWithSelector
        import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.VersionParser

        afterEvaluate {
            def gradleVersion = project.gradle.gradleVersion
            task printVersion {
                doLast {
                    println "Gradle version: " + gradleVersion
                    println "Version from Selector: " + new VersionWithSelector("1.0.0", new VersionParser().transform("1.0.0")).asSelector().selector
                }
            }
        }
        """
                )
            }
        }

        val result = testProject.run("printVersion") {
            withGradleVersion(gradleVersionToUse)
        }

        assertThat(result.output).contains("Gradle version: " + gradleVersionToUse)
        assertThat(result.output).contains("Version from Selector: 1.0.0")
    }
}