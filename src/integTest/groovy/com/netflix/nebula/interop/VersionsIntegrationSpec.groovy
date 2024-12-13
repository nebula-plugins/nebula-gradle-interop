package com.netflix.nebula.interop

import nebula.test.IntegrationSpec
import org.gradle.util.GradleVersion
import spock.lang.IgnoreIf
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
    @IgnoreIf({ jvm.isJava9Compatible() })
    def 'VersionWithSelector parses selector switching to proper DefaultVersionSelectorScheme constructor - Legacy - Gradle #gradleVersionToUse'() {
        setup:
        gradleVersion = gradleVersionToUse

        buildFile << """
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

        when:
        def result = runTasksSuccessfully('printVersion')

        then:
        result.standardOutput.contains("Gradle version: " + gradleVersionToUse)
        result.standardOutput.contains("Version from Selector: 1.0.0")

        where:
        gradleVersionToUse << [
                '4.4',
                '4.5'
        ]
    }

    @Unroll
    @IgnoreIf({ jvm.isJava17Compatible() })
    def 'VersionWithSelector parses selector switching to proper DefaultVersionSelectorScheme constructor - older gradle - Gradle #gradleVersionToUse'() {
        setup:
        gradleVersion = gradleVersionToUse

        buildFile << """
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

        when:
        def result = runTasksSuccessfully('printVersion')

        then:
        result.standardOutput.contains("Gradle version: " + gradleVersionToUse)
        result.standardOutput.contains("Version from Selector: 1.0.0")

        where:
        gradleVersionToUse << [
                '5.4',
                '5.5',
                '5.6',
                '6.1',
                '6.9',
                '7.0',
                '7.1',
                '7.2',
                '7.3'
        ]
    }

    @Unroll
    def 'VersionWithSelector parses selector switching to proper DefaultVersionSelectorScheme constructor - modern - Gradle #gradleVersionToUse'() {
        setup:
        gradleVersion = gradleVersionToUse

        buildFile << """
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

        when:
        def result = runTasksSuccessfully('printVersion')

        then:
        result.standardOutput.contains("Gradle version: " + gradleVersionToUse)
        result.standardOutput.contains("Version from Selector: 1.0.0")

        where:
        gradleVersionToUse << [
                GradleVersion.current().version
        ]
    }
}
