plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    id("maven-publish")
}

group = "com.degoos"
version = "0.0.1-SNAPSHOT"

val hytaleServerExecutablePath: String by extra

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.kotlin.reflect)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.serialization.json)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(23)
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveBaseName.set("Kayle")
}

group = "com.degoos"
version = "1.0.0"

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            groupId = "com.degoos"
            artifactId = "kayle"
            version = "1.0.0"
        }
    }

    repositories {
        maven {
            name = "LocalBuildDir"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}