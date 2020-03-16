package org.jetbrains.internship

import org.gradle.api.Plugin
import org.gradle.api.Project

class HashSumPlugin : Plugin<Project> {
    override fun apply(p0: Project) {

        p0.tasks.create("calculateSha1") {
        }

        //TODO replace with your implementation
        println("HashingPlugin applied!")
    }
}