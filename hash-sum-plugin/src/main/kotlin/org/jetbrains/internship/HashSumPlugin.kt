package org.jetbrains.internship

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.util.PatternSet
import java.io.SequenceInputStream
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.stream.Collectors

class HashSumPlugin : Plugin<Project> {
    override fun apply(p0: Project) {
        p0.tasks.create("calculateSha1") {
            p0.allprojects.stream().forEach { calculateSha1(it) }
        }
    }

    private fun calculateFiles(p0: Project) {
        val filePattern = PatternSet().include("**/*.java", "**/*.kt")
        val fileNames = p0.fileTree(p0.projectDir)
            .matching(filePattern)
            .files.stream()
            .map { it.name }
            .collect(Collectors.joining(", "))

        if (!Files.exists(p0.buildDir.toPath())) {
            Files.createDirectory(p0.buildDir.toPath())
        }
        Files.writeString(p0.buildDir.resolve("hash-sum-plugin").toPath(), fileNames)

    }

    private fun calculateSha1(p0: Project) {
        val md = MessageDigest.getInstance("SHA-1")
        val filePattern = PatternSet().include("**/*.java", "**/*.kt")
        val inputStream = p0.fileTree(p0.projectDir)
            .matching(filePattern)
            .files.stream()
            .map { Files.newInputStream(it.toPath()) }
            .reduce { is1, is2 -> SequenceInputStream(is1, is2) }
            .get()

        val dis = DigestInputStream(inputStream, md)

        dis.use {
            val buffer = ByteArray(256 * 1024)
            @Suppress("ControlFlowWithEmptyBody")
            while (dis.read(buffer) != -1) {
            }
        }

        val result = dis.messageDigest.digest().toHexString()

        if (!Files.exists(p0.buildDir.toPath())) {
            Files.createDirectory(p0.buildDir.toPath())
        }
        Files.writeString(p0.buildDir.resolve("hash-sum-plugin").toPath(), result)
    }

    private fun ByteArray.toHexString(): String {
        return this.joinToString("") {
            java.lang.String.format("%02x", it)
        }
    }
}