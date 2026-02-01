plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    id("maven-publish")
}

val kayleVersion = "0.0.7"

group = "com.degoos"
version = kayleVersion

val hytaleServerExecutablePath: String by extra

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.kotlin.reflect)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.serialization.json)

    implementation(libs.caffeine)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(23)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveBaseName.set("Kayle")
}

tasks.named<ProcessResources>("processResources") {
    val replaceProperties = mapOf(
        "plugin_name" to "Kayle",
        "plugin_version" to project.version,
    )

    filesMatching("manifest.json") {
        expand(replaceProperties)
    }

    inputs.properties(replaceProperties)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            groupId = "com.degoos"
            artifactId = "kayle"
            version = kayleVersion
        }
    }

    repositories {
        maven {
            name = "LocalBuildDir"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}