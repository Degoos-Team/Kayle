import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
}

group = "com.degoos"
version = "0.0.2-SNAPSHOT"

val hytaleInstallationPath: String by extra
val hytaleServerExecutablePath: String by extra
val serverOutputPath: String by extra


dependencies {
    compileOnly(project(":Core"))
    compileOnly(files(hytaleServerExecutablePath))

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(23)
}

tasks.test {
    useJUnitPlatform()
}


tasks.shadowJar {
    archiveBaseName.set("KayleExamples")
    dependencies {
        exclude(dependency("org.jetbrains.kotlin:.*"))
        exclude(dependency("org.jetbrains.kotlinx:.*"))
    }
}