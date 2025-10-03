package com.netflix.nebula.interop;

import nebula.test.AbstractIntegrationTestKitBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

internal class ConfigurationsTest : AbstractIntegrationTestKitBase() {
    @BeforeEach
    fun init(testInfo: TestInfo) {
        super.initialize(javaClass, testInfo.getDisplayName())
        debug = true
        keepFiles = true
        definePluginOutsideOfPluginBlock = true
    }

    @AfterEach
    fun cleanup() {
        traitCleanup()
    }

    @ParameterizedTest
    @ValueSource(strings = ["groovy", "kotlin"])
    fun `onResolve performs an action once a configuration is resolved using a buildSrc plugin`(pluginLanguage: String) {
        buildFile.writeText("""
        import myplugin.MyPlugin
        plugins {
            id 'java'
        }
        apply plugin: MyPlugin
    """.trimIndent())

        if(pluginLanguage == "groovy") {
            setupGroovyBuildSrcPlugin(projectDir)
        } else {
            setupKotlinBuildSrcPlugin(projectDir)
        }

        val result =runTasks("dependencies", "--configuration", "compileClasspath", "--warning-mode", "none", "--info")

        assertThat(result.output).contains("Resolved configuration 'compileClasspath' from MyPlugin")

        assertThat(result.output).contains("Reached configuration 'annotationProcessor' from MyPlugin")
                assertThat(result.output).contains("Reached configuration 'compileClasspath' from MyPlugin")
                assertThat(result.output).contains("Reached configuration 'runtimeClasspath' from MyPlugin")
    }

    @ParameterizedTest
    @ValueSource(strings = ["groovy", "kotlin"])
    fun `onResolve does not perform an action if a configuration is not resolved  - using a buildSrc plugin`(pluginLanguage: String) {
        buildFile.writeText("""
        import myplugin.MyPlugin
        plugins {
            id 'java'
        }
        apply plugin: MyPlugin
    """.trimIndent())
        if(pluginLanguage == "groovy") {
            setupGroovyBuildSrcPlugin(projectDir)
        } else {
            setupKotlinBuildSrcPlugin(projectDir)
        }

        val result = runTasks("help", "--warning-mode", "none")

        assertThat(result.output).contains("> Task :help")
        assertThat(result.output).doesNotContain("Resolved configuration 'compileClasspath' from MyPlugin")

        assertThat(result.output).contains("Reached configuration 'annotationProcessor' from MyPlugin")
                assertThat(result.output).contains("Reached configuration 'compileClasspath' from MyPlugin")
                assertThat(result.output).contains("Reached configuration 'runtimeClasspath' from MyPlugin")
    }

    private fun setupGroovyBuildSrcPlugin(projectDir: File) {
        val buildSrcDir = projectDir.resolve("buildSrc")
        buildSrcDir.mkdirs()
        val buildSrcBuildFile =buildSrcDir.resolve("build.gradle")
        buildSrcBuildFile.writeText("""
            plugins {
                id 'groovy'
                id 'java-library'
            }
            repositories {
                mavenCentral()
            }
            dependencies {
                api 'com.netflix.nebula:nebula-gradle-interop:latest.release'
            }
            """.trimIndent())
        val pluginDir = buildSrcDir.resolve("src/main/groovy/myplugin")
        pluginDir.mkdirs()
        val pluginFile = pluginDir.resolve("MyPlugin.groovy")
        pluginFile.writeText(
            $$"""
            package myplugin

            import com.netflix.nebula.interop.ConfigurationsKt
            import kotlin.Unit
            import kotlin.jvm.functions.Function1
            import org.gradle.api.Plugin
            import org.gradle.api.Project
            import org.gradle.api.artifacts.ResolvableDependencies

            class MyPlugin implements  Plugin<Project> {
                @Override
                void apply(Project project) {
                    project.configurations.all { conf ->
                        ConfigurationsKt.onResolve(conf, new Function1<ResolvableDependencies, Unit>() {
                            @Override
                            Unit invoke(ResolvableDependencies resolvableDependencies) {
                                println("Resolved configuration '${conf.name}' from MyPlugin")
                                return Unit.INSTANCE;
                            }
                        });
                        println("Reached configuration '${conf.name}' from MyPlugin")
                    }
                }
            }
            """.trimIndent())
    }

    private fun setupKotlinBuildSrcPlugin(projectDir: File) {
        val buildSrcDir = projectDir.resolve("buildSrc")
        buildSrcDir.mkdirs()
        val buildSrcBuildFile =buildSrcDir.resolve("build.gradle")
        buildSrcBuildFile.writeText("""
            plugins {
                id 'java-library'
                id "org.jetbrains.kotlin.jvm" version "2.2.0"
            }
            repositories {
                mavenCentral()
            }
            dependencies {
                api 'com.netflix.nebula:nebula-gradle-interop:latest.release'
            }
            """.trimIndent())
        val pluginDir = buildSrcDir.resolve("src/main/kotlin/myplugin")
        pluginDir.mkdirs()
        val pluginFile = pluginDir.resolve("MyPlugin.kt")
        pluginFile.writeText(
            $$"""
            package myplugin

            import com.netflix.nebula.interop.onResolve
            import org.gradle.api.Plugin
            import org.gradle.api.Project

            class MyPlugin : Plugin<Project> {
                override fun apply(project: Project) {
                    project.configurations.all { conf : org.gradle.api.artifacts.Configuration ->
                        conf.onResolve {
                            println("Resolved configuration '${conf.name}' from MyPlugin")
                        }
                        println("Reached configuration '${conf.name}' from MyPlugin")
                    }
                }
            }
            """.trimIndent())
    }
}
