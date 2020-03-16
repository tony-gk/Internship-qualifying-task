plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    testImplementation(gradleApi())
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}