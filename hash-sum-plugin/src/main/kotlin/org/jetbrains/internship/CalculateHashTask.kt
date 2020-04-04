package org.jetbrains.internship

import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet
import java.io.InputStream
import java.io.SequenceInputStream
import java.nio.file.Files
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject

open class CalculateHashTask
@Inject constructor(private val algorithm: String) : DefaultTask() {

    @TaskAction
    fun doAction() {
        project.allprojects.stream().forEach { calculateHash(it) }
    }

    private fun calculateHash(p: Project) {
        val digest = calculateDigest(getFilesInputStream(p), getMessageDigest())
        writeDigest(p, digest)
    }

    private fun writeDigest(p: Project, digest: String) {
        val buildDirPath = p.buildDir.toPath()

        Files.createDirectories(buildDirPath.resolve(HashSumPlugin.OUTPUT_DIR_NAME))

        Files.writeString(
            buildDirPath.resolve(HashSumPlugin.OUTPUT_DIR_NAME).resolve(HashSumPlugin.OUTPUT_FILE_NAME),
            digest
        )
    }

    private fun getMessageDigest(): MessageDigest {
        return try {
            MessageDigest.getInstance(algorithm)
        } catch (e: NoSuchAlgorithmException) {
            throw InvalidUserDataException(
                "Please select one of the following hash algorithms: " +
                        "MD2, MD5, SHA-1, SHA-224, SHA-256, SHA-384, SHA-512"
            )
        }
    }

    private fun calculateDigest(filesInputStream: InputStream, messageDigest: MessageDigest): String {
        val dis = DigestInputStream(filesInputStream, messageDigest)

        dis.use {
            val buffer = ByteArray(256 * 1024)
            @Suppress("ControlFlowWithEmptyBody")
            while (dis.read(buffer) != -1) {
            }
        }

        return dis.messageDigest.digest().toHexString()
    }

    private fun getFilesInputStream(p: Project): InputStream {
        val filePattern = PatternSet().include("**/*.java", "**/*.kt")
        val inputStreamList = p.fileTree(p.projectDir)
            .matching(filePattern)
            .files.stream()
            .map { Files.newInputStream(it.toPath()) }
            .collect(Collectors.toList())
        return SequenceInputStream(Collections.enumeration(inputStreamList))
    }

    private fun ByteArray.toHexString(): String {
        return this.joinToString("") {
            java.lang.String.format("%02x", it)
        }
    }
}