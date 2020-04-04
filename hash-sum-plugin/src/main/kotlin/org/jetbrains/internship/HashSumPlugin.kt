package org.jetbrains.internship

import org.gradle.api.Plugin
import org.gradle.api.Project


class HashSumPlugin : Plugin<Project> {
    companion object {
        const val EXTENSION_NAME = "hashsum"
        const val TASK_NAME = "calculateHash"
        const val DEFAULT_ALGORITHM = "SHA-1"
    }

    override fun apply(project: Project) {
        with(project) {
            val extension = extensions.create(
                EXTENSION_NAME,
                HashSumExtension::class.java,
                DEFAULT_ALGORITHM
            )

            afterEvaluate {
                tasks.register(TASK_NAME, CalculateHashTask::class.java, extension.algorithm)
            }
        }

    }
}