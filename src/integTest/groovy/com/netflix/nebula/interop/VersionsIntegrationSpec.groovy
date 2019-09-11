package com.netflix.nebula.interop

import nebula.test.IntegrationSpec
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
    def 'VersionWithSelector parses selector switching to proper DefaultVersionSelectorScheme constructor'() {
        setup:
        gradleVersion = gradleVersionToUse

        buildFile << """
        import com.netflix.nebula.interop.VersionWithSelector
        import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.VersionParser

        task printVersion {
            doLast {
                println "Version from Selector: " + new VersionWithSelector("1.0.0", new VersionParser().transform("1.0.0")).asSelector().selector
            }
        }
        """

        when:
        def result = runTasksSuccessfully('printVersion')

        then:
        result.standardOutput.contains("Version from Selector: " + version)

        where:
        version | gradleVersionToUse
        '1.0.0' | '5.6.2'
        '1.0.0' | '4.0'
    }
}
