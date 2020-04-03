package org.jetbrains.internship

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.util.PatternSet
import java.io.InputStream
import java.io.SequenceInputStream
import java.nio.file.Files
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.*
import java.util.stream.Collectors


class HashSumPlugin : Plugin<Project> {
    companion object {
        const val HASH_ALGORITHM = "SHA-1"
        const val TASK_NAME = "calculateSha1"
        const val OUTPUT_DIR_NAME = "hashsum-plugin"
        const val OUTPUT_FILE_NAME = "hashsum"
    }

    override fun apply(project: Project) {
        project.tasks.create(TASK_NAME) {
        }

        project.tasks.findByName(TASK_NAME)!!.doFirst {
            project.allprojects.stream().forEach { calculateSha1(it) }
        }
    }

    private fun calculateSha1(project: Project) {
        val digest = calculateDigest(getFilesInputStream(project))
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

    private fun calculateDigest(filesInputStream: InputStream): String {
        val messageDigest = MessageDigest.getInstance(HASH_ALGORITHM)
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
