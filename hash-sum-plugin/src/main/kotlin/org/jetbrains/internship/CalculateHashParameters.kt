package org.jetbrains.internship

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters

@Suppress("UnstableApiUsage")
interface CalculateHashParameters : WorkParameters {
    val projectDirectory: DirectoryProperty
    val outputFile: RegularFileProperty
    val projectName: Property<String>
    val hashAlgorithm: Property<String>
}