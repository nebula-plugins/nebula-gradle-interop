package com.netflix.nebula.interop

import nebula.test.IntegrationSpec
import org.gradle.util.GradleVersion
import spock.lang.Unroll

class VersionsIntegrationSpec extends IntegrationSpec {

    def setup() {
        def initScript = new File(projectDir, "init.gradle")
        initScript.text = """
            initscript {
                dependencies {
                   ${DependenciesBuilderWithClassesUnderTest.buildDependencies()}
                }
            }           
        """
        addInitScript(initScript)
    }

    @Unroll
    def 'VersionWithSelector parses selector switching to proper DefaultVersionSelectorScheme constructor - Gradle #gradleVersionToUse'() {
        setup:
        gradleVersion = gradleVersionToUse

        buildFile << """
        import com.netflix.nebula.interop.VersionWithSelector
        import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.VersionParser

        task printVersion {
            doLast {
                println "Gradle version: " + project.gradle.gradleVersion
                println "Version from Selector: " + new VersionWithSelector("1.0.0", new VersionParser().transform("1.0.0")).asSelector().selector
            }
        }
        """

        when:
        def result = runTasksSuccessfully('printVersion')

        then:
        result.standardOutput.contains("Gradle version: " + gradleVersionToUse)
        result.standardOutput.contains("Version from Selector: 1.0.0")

        where:
        gradleVersionToUse << [
                '4.0',
                '4.1',
                '4.2',
                '4.3',
                '4.4',
                '4.5',
                '5.0',
                '5.1',
                '5.2',
                '5.3',
                '5.4',
                '5.5',
                '5.6',
                GradleVersion.current().version
        ]
    }
}
