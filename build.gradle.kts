import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.shadow) apply false
}

val hytalePathProp: Provider<String> = providers.gradleProperty("hytale.path")
val serverPathProp: Provider<String> = providers.gradleProperty("hytale.server")

subprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)

    repositories {
        mavenCentral()
    }

    val hytaleJar = hytalePathProp.map { "$it/Server/HytaleServer.jar" }.getOrElse("INVALID_PATH")
    val modsDir = serverPathProp.map { "$it/mods" }.getOrElse("build/default-mods")

    extra.apply {
        set("hytaleServerExecutablePath", hytaleJar)
        set("serverOutputPath", modsDir)
    }

    dependencies {
        if (hytalePathProp.isPresent) {
            add("compileOnly", files(hytaleJar))
        }
    }

    tasks.withType<ShadowJar> {
        archiveBaseName.set(project.name)
        archiveClassifier.set("")
    }

    // Deploy Task - Only registered if the project actually has the Shadow plugin applied
    pluginManager.withPlugin("com.github.johnrengelman.shadow") {
        tasks.register<Copy>("deployPlugin") {
            group = "deployment"
            description = "Builds the ShadowJar and copies it to the external server."

            dependsOn("shadowJar")

            val shadowTask = tasks.named<ShadowJar>("shadowJar")
            from(shadowTask.flatMap { it.archiveFile })
            into(modsDir)

            doLast {
                println(">> [${project.name}] Deployed to: $modsDir")
            }
        }
    }
}