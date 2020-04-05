package org.jetbrains.internship

import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.*
import org.gradle.api.tasks.util.PatternSet
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkerExecutor
import java.io.File
import java.nio.file.Files
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class CalculateHashTask : DefaultTask() {
    companion object {
        const val OUTPUT_DIR_NAME = "hash-sum-plugin"

        const val OUTPUT_FILE_PREFIX = "hash-sum"

        val SUPPORTED_ALGORITHMS: Array<String>
            get() = arrayOf("MD2", "MD5", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512")
    }

    private val outputFileName: String
        get() = "$OUTPUT_FILE_PREFIX.$algorithm"

    @get:Inject
    open val workerExecutor: WorkerExecutor
        get() {
            // Getter body is ignored
            throw UnsupportedOperationException()
        }

    @get:Input
    var algorithm: String = ""

    @get:Incremental
    @get:InputFiles
    val inputFiles: FileTree
        get() = project.fileTree(project.projectDir)
            .matching(PatternSet().include("**/*.java", "**/*.kt"))

    @get:Incremental
    @get:OutputFiles
    val outputFiles: List<File>
        get() = project.allprojects
            .map { it.buildDir.resolve(OUTPUT_DIR_NAME).resolve(outputFileName) }
            .toList()


    @TaskAction
    fun calculateHash() {
        checkAlgorithm()
        val workQueue = workerExecutor.noIsolation()

        project.allprojects.forEach { p ->
            val outputDirectory = p.buildDir.toPath().resolve(OUTPUT_DIR_NAME)
            Files.createDirectories(outputDirectory)

            workQueue.submit(CalculateHashAction::class.java) {
                outputFile.set(outputDirectory.resolve(outputFileName).toFile())
                projectDirectory.set(p.projectDir)
                hashAlgorithm.set(algorithm)
                projectName.set(p.name)
            }
        }
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

}
