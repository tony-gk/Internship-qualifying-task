package org.jetbrains.internship

import org.gradle.api.Plugin
import org.gradle.api.Project


class HashSumPlugin : Plugin<Project> {
    companion object {
        const val EXTENSION_NAME = "hashsum"
        const val TASK_NAME = "calculateHash"
        const val OUTPUT_DIR_NAME = "hash-sum-plugin"
        const val OUTPUT_FILE_NAME = "hash-sum"
    }

    override fun apply(project: Project) {
        with(project) {
            val extension = extensions.create(
                EXTENSION_NAME,
                HashSumExtension::class.java
            )

            afterEvaluate {
                tasks.register(TASK_NAME, CalculateHashTask::class.java, extension.algorithm)
            }
        }

    }
}