plugins {
    kotlin("jvm") version "1.3.61"
}

repositories {
    jcenter()
}

buildscript {
    dependencies {
        classpath("org.jetbrains:hash-sum-plugin:1.0")
    }
}

apply(plugin = "org.jetbrains.internship")

