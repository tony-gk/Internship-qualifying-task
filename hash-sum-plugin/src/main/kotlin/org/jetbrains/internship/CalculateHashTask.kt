package org.jetbrains.internship

import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.*
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkerExecutor
import java.io.File
import java.nio.file.Files
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class CalculateHashTask
@Inject constructor(private val workerExecutor: WorkerExecutor) : DefaultTask() {

    @get:Input
    var algorithm: String = ""

    companion object {
        const val OUTPUT_DIR_NAME = "hash-sum-plugin"

        const val OUTPUT_FILE_NAME = "hash-sum"

        val SUPPORTED_ALGORITHMS: Array<String>
            get() = arrayOf("MD2", "MD5", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512")
    }


    private fun checkAlgorithm() {
        if (!SUPPORTED_ALGORITHMS.contains(algorithm)) {
            throw InvalidUserDataException(
                "Unknown algorithm: $algorithm\n" +
                        "Please select one of the following hash algorithms: "
                        + SUPPORTED_ALGORITHMS.joinToString((", "))
            )
        }
    }

    @TaskAction
    fun calculateHash() {
        checkAlgorithm()
        val workQueue = workerExecutor.noIsolation()

        project.allprojects.forEach { p ->
            val outputDirectory = p.buildDir.toPath().resolve(OUTPUT_DIR_NAME)
            Files.createDirectories(outputDirectory)

            workQueue.submit(CalculateHashAction::class.java) {
                outputFile.set(outputDirectory.resolve(OUTPUT_FILE_NAME).toFile())
                projectDirectory.set(p.projectDir)
                hashAlgorithm.set(algorithm)
                projectName.set(p.name)
            }
        }
    }

}
