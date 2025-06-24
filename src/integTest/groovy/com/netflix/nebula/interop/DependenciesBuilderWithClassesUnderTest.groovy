package com.netflix.nebula.interop

import groovy.json.StringEscapeUtils
import nebula.test.functional.GradleRunner
import org.gradle.internal.classloader.ClasspathUtil
import org.gradle.internal.classpath.ClassPath

import java.util.function.Predicate

class DependenciesBuilderWithClassesUnderTest {
    static String buildDependencies() {
        ClassLoader classLoader = DependenciesBuilderWithClassesUnderTest.class.getClassLoader()
        def classpathFilter = GradleRunner.CLASSPATH_DEFAULT
        getClasspathAsFiles(classLoader, classpathFilter).collect {
            String.format("      classpath files('%s')\n", escapeString(it.getAbsolutePath()))
        }.join('\n')
    }

    private static List<File> getClasspathAsFiles(ClassLoader classLoader, Predicate<URL> classpathFilter) {
        List<URL> classpathUrls = getClasspathUrls(classLoader)
        return classpathUrls.findAll {classpathFilter.test(it)}.collect {return new File(it.toURI()) }
    }

    private static List<URL> getClasspathUrls(ClassLoader classLoader) {
        Object cp = ClasspathUtil.getClasspath(classLoader)
        if (cp instanceof List<URL>) {
            return (List<URL>) cp
        }
        if (cp instanceof ClassPath) {
            return ((ClassPath) cp).asURLs
        }
        throw new IllegalStateException("Unable to extract classpath urls from type ${cp.class.canonicalName}")
    }

    private static String escapeString(Object obj) {
        return obj == null ? null : StringEscapeUtils.escapeJava(obj.toString());
    }

}