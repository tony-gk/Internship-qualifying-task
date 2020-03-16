package org.jetbrains.internship

import org.gradle.api.Project
import org.junit.jupiter.api.Test
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertTrue

class HashSumPluginTest {
    @Test
    fun basicTest() {
        val project: Project = ProjectBuilder.builder().build()
        with(project.pluginManager) {
            apply("org.jetbrains.internship")
            assertTrue(hasPlugin("org.jetbrains.internship"))
        }
    }
}