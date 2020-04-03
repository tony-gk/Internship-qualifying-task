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

    override fun apply(p0: Project) {
        p0.tasks.create("calculateSha1") {
            p0.allprojects.stream().forEach { calculateSha1(it) }
        }
    }

    private fun calculateSha1(p: Project) {
        val md = MessageDigest.getInstance("SHA-1")
        val dis = DigestInputStream(getFilesInputStream(p), md)

        dis.use {
            val buffer = ByteArray(256 * 1024)
            @Suppress("ControlFlowWithEmptyBody")
            while (dis.read(buffer) != -1) {
            }
        }

        val digest = dis.messageDigest.digest().toHexString()

        if (!Files.exists(p.buildDir.toPath())) {
            Files.createDirectory(p.buildDir.toPath())
        }
        Files.writeString(p.buildDir.resolve("hash-sum-plugin").toPath(), digest)
    }

    private fun getFilesInputStream(p: Project): InputStream? {
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