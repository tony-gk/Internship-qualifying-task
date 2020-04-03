package org.jetbrains.internship

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.util.PatternSet
import java.io.InputStream
import java.io.SequenceInputStream
import java.nio.file.Files
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.stream.Collectors


class HashSumPlugin : Plugin<Project> {
    companion object {
        const val EXTENSION_NAME = "hashsum"
        const val TASK_NAME = "calculateHash"
        const val OUTPUT_DIR_NAME = "hashsum-plugin"
        const val OUTPUT_FILE_NAME = "hashsum"
    }

    override fun apply(project: Project) {
        with(project) {
            val extension = extensions.create(
                EXTENSION_NAME,
                HashSumExtension::class.java
            )

            tasks.create(TASK_NAME) {
            }

            tasks.findByName(TASK_NAME)!!.doFirst {
                allprojects.stream().forEach { calculateSha1(it, extension.algorithm) }
            }
        }

    }

    private fun calculateSha1(project: Project, algorithm: String) {
        val digest = calculateDigest(getFilesInputStream(project), getMessageDigest(algorithm))
        writeDigest(project, digest)
    }

    private fun writeDigest(project: Project, digest: String) {
        val buildDirPath = project.buildDir.toPath()

        Files.createDirectories(buildDirPath.resolve(OUTPUT_DIR_NAME))

        Files.writeString(
            buildDirPath.resolve(OUTPUT_DIR_NAME).resolve(OUTPUT_FILE_NAME),
            digest
        )
    }

    private fun getMessageDigest(algorithm: String): MessageDigest {
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