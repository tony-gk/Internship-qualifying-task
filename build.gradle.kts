plugins {
    kotlin("jvm") version "1.3.61"
}

repositories {
    jcenter()
}

buildscript {
    dependencies {
        classpath("org.jetbrains:hash-sum-plugin:1.2")
    }
}

apply(plugin = "org.jetbrains.internship")

configure<org.jetbrains.internship.HashSumExtension> {
    algorithm = "SHA-512"
}
