plugins {
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    testImplementation(gradleTestKit())
    testImplementation("junit", "junit", "4.12")
}

publishing {
    publications {
        create<MavenPublication>("pluginPublication") {
            groupId = "org.jetbrains"
            artifactId = "hash-sum-plugin"
            version = "1.1"
            from(components["java"])
        }
    }
}
