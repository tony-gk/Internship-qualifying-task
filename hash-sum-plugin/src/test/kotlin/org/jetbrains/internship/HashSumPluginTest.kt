package org.jetbrains.internship

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class HashSumPluginTest {
    companion object {
        val HASH_FILE_PATH: String
            get() = "hashsum-plugin${File.separator}hashsum"
        const val CALCULATE_HASH_TASK_NAME = "calculateHash"
        const val EXTENSION_NAME = "hashsum"

        val String.isSHA1Content: Boolean
            get() = matches("^[a-fA-F0-9]{40}$".toRegex())

        val hashDigestLengthMap: Map<String, Int>
            get() = mapOf(
                "SHA-1" to 160, "SHA-224" to 224, "SHA-256" to 256,
                "SHA-384" to 384, "SHA-512" to 512, "MD2" to 128,
                "MD5" to 128
            )
    }

    private val exampleProjectDir: File =
        File(System.getProperty("user.dir")).parentFile.resolve("example-project")

    private val project: Project = ProjectBuilder.builder().withProjectDir(exampleProjectDir).build()

    private fun runTask(task: Task) {
        task.actions.forEach {
            it.execute(task)
        }
    }

    private fun getHashRegex(hexStringLength: Int): Regex {
        return ("^[a-fA-F0-9]{$hexStringLength}$").toRegex()
    }

    @Before
    fun setUp() {
        project.plugins.apply(HashSumPlugin::class.java)
    }

    @Test
    fun basicTest() {
        with(project.plugins) {
            assertTrue(hasPlugin(HashSumPlugin::class.java))
        }
    }

    @Test
    fun validateExtensionTest() {
        val extension = project.extensions.getByName(EXTENSION_NAME)
        assertNotNull(extension)
        assertTrue(extension is HashSumExtension)
    }

    @Test
    fun defaultAlgorithmTest() {
        val calculateHashTask = project.tasks.findByPath(CALCULATE_HASH_TASK_NAME)
        assertNotNull(calculateHashTask)

        runTask(calculateHashTask!!)
        val hashSumFile = project.buildDir.resolve(HASH_FILE_PATH)
        assertTrue(hashSumFile.exists() && hashSumFile.readText().isSHA1Content)
    }

    @Test
    fun variousAlgorithmsTest() {
        val calculateHashTask = project.tasks.findByPath(CALCULATE_HASH_TASK_NAME)!!
        val hashSumFile = project.buildDir.resolve(HASH_FILE_PATH)

        val extension = project.extensions.getByName(EXTENSION_NAME) as HashSumExtension

        for ((name, length) in hashDigestLengthMap) {
            val hashRegex = getHashRegex(length / 4)

            extension.algorithm = name
            runTask(calculateHashTask)

            val digest = hashSumFile.readText()
            assertTrue(
                "Digest doesn't match regex\nAlgorithm: $name\n Digest: $digest",
                digest.matches(hashRegex)
            )
        }
    }

}

