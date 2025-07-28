/**
 *
 *  Copyright 2020 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.netflix.nebula.interop

import nebula.test.IntegrationTestKitSpec
import spock.lang.Subject
import spock.lang.Unroll

@Subject(ConfigurationsKt)
class ConfigurationsSpec extends IntegrationTestKitSpec {
    def setup() {
        debug = true
        keepFiles = true
        definePluginOutsideOfPluginBlock = true
    }

    @Unroll
    def "onResolve performs an action once a configuration is resolved - using a #pluginLanguage buildSrc plugin"() {
        given:
        buildFile << """
            import myplugin.MyPlugin
            plugins {
                id 'java'
            }
            apply plugin: MyPlugin
            """.stripIndent()
        if(pluginLanguage == 'groovy') {
            setupGroovyBuildSrcPlugin()
        } else {
            setupKotlinBuildSrcPlugin()
        }

        when:
        def result = runTasks('dependencies', '--configuration', 'compileClasspath', '--warning-mode', 'none')

        then:
        result.output.contains("Resolved configuration 'compileClasspath' from MyPlugin")

        result.output.contains("Reached configuration 'annotationProcessor' from MyPlugin")
        result.output.contains("Reached configuration 'compileClasspath' from MyPlugin")
        result.output.contains("Reached configuration 'runtimeClasspath' from MyPlugin")

        where:
        pluginLanguage << ['groovy', 'kotlin']
    }

    @Unroll
    def "onResolve does not perform an action if a configuration is not resolved  - using a #pluginLanguage buildSrc plugin"() {
        given:
        buildFile << """
            import myplugin.MyPlugin
            plugins {
                id 'java'
            }
            apply plugin: MyPlugin
            """.stripIndent()
        if(pluginLanguage == 'groovy') {
            setupGroovyBuildSrcPlugin()
        } else {
            setupKotlinBuildSrcPlugin()
        }

        when:
        def result = runTasks('help', '--warning-mode', 'none')

        then:
        result.output.contains("> Task :help")
        !result.output.contains("Resolved configuration 'compileClasspath' from MyPlugin")

        result.output.contains("Reached configuration 'annotationProcessor' from MyPlugin")
        result.output.contains("Reached configuration 'compileClasspath' from MyPlugin")
        result.output.contains("Reached configuration 'runtimeClasspath' from MyPlugin")

        where:
        pluginLanguage << ['groovy', 'kotlin']
    }

    private void setupGroovyBuildSrcPlugin() {
        def buildSrcDir = new File(projectDir, 'buildSrc')
        buildSrcDir.mkdirs()
        def buildSrcBuildFile = new File(buildSrcDir, 'build.gradle')
        buildSrcBuildFile << """
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
            """.stripIndent()
        def pluginDir = new File(buildSrcDir, 'src/main/groovy/myplugin')
        pluginDir.mkdirs()
        def pluginFile = new File(pluginDir, 'MyPlugin.groovy')
        pluginFile << """
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
                                println("Resolved configuration '\${conf.name}' from MyPlugin")
                                return Unit.INSTANCE;
                            }
                        });
                        println("Reached configuration '\${conf.name}' from MyPlugin")
                    }
                }
            }
            """.stripIndent()
    }

    private void setupKotlinBuildSrcPlugin() {
        def buildSrcDir = new File(projectDir, 'buildSrc')
        buildSrcDir.mkdirs()
        def buildSrcBuildFile = new File(buildSrcDir, 'build.gradle')
        buildSrcBuildFile << """
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
            """.stripIndent()
        def pluginDir = new File(buildSrcDir, 'src/main/kotlin/myplugin')
        pluginDir.mkdirs()
        def pluginFile = new File(pluginDir, 'MyPlugin.kt')
        pluginFile << """
            package myplugin

            import com.netflix.nebula.interop.onResolve
            import org.gradle.api.Plugin
            import org.gradle.api.Project
            
            class MyPlugin : Plugin<Project> {
                override fun apply(project: Project) {
                    project.configurations.all { conf : org.gradle.api.artifacts.Configuration ->
                        conf.onResolve {
                            println("Resolved configuration '\${conf.name}' from MyPlugin")
                        }
                        println("Reached configuration '\${conf.name}' from MyPlugin")
                    }
                }
            }
            """.stripIndent()
    }
}
