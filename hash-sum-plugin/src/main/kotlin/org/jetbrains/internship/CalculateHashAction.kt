package org.jetbrains.internship

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.util.PatternSet
import org.gradle.workers.WorkAction
import java.io.InputStream
import java.io.SequenceInputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.*

@Suppress("UnstableApiUsage")
abstract class CalculateHashAction : WorkAction<CalculateHashParameters> {
    override fun execute() {
        val fileTree = parameters.projectDirectory.asFileTree
        val outputFile = parameters.outputFile.asFile.get()
        val algorithm = parameters.hashAlgorithm.get()

        val digest = calculateDigest(getFilesInputStream(fileTree), algorithm)
        outputFile.writeText(digest)

        println("Hash of files in project '${parameters.projectName.get()}' calculated")
    }

    private fun calculateDigest(filesInputStream: InputStream, algorithm: String): String {
        val dis = DigestInputStream(filesInputStream, MessageDigest.getInstance(algorithm))

        dis.use {
            val buffer = ByteArray(256 * 1024)
            @Suppress("ControlFlowWithEmptyBody")
            while (dis.read(buffer) != -1) {
            }
        }

        return dis.messageDigest.digest().toHexString()
    }

    private fun getFilesInputStream(files: FileTree): InputStream {
        val filePattern = PatternSet().include("**/*.java", "**/*.kt")
        val inputStreamList = files
            .matching(filePattern)
            .map { it.inputStream()}
            .toMutableList()
        return SequenceInputStream(Collections.enumeration(inputStreamList))
    }

    private fun ByteArray.toHexString(): String {
        return this.joinToString("") {
            java.lang.String.format("%02x", it)
        }
    }
}