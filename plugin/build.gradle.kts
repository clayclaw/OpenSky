import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
    id("com.google.devtools.ksp") version "1.9.22-1.0.18"
    id("kr.entree.spigradle") version "2.4.3"
}

group = "io.github.clayclaw"
version = "0.0.1-ALPHA"

repositories {
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/central")
    }
    maven {
        url = uri("https://repo.infernalsuite.com/repository/maven-snapshots/")
    }
    maven {
        url = uri("https://repo.rapture.pw/repository/maven-releases/")
    }
}

val kotlinVersion: String by project
val spigotVersion: String by project
val exposedVersion: String by project
val koinVersion: String by project
val koinAnnotationVersion: String by project
val hopliteVersion: String by project
val cliktVersion: String by project
val hikariCpVersion: String by project
val gsonVersion: String by project
val snakeYamlVersion: String by project
val kredsVersion: String by project
val mccourtineVersion: String by project

val mavenDependencies = listOf(
    "io.insert-koin:koin-core:$koinVersion",
    "io.insert-koin:koin-annotations:$koinAnnotationVersion",
    "com.github.ajalt.clikt:clikt:$cliktVersion",
    "org.jetbrains.exposed:exposed-core:$exposedVersion",
    "org.jetbrains.exposed:exposed-dao:$exposedVersion",
    "org.jetbrains.exposed:exposed-jdbc:$exposedVersion",
    "org.jetbrains.exposed:exposed-jodatime:$exposedVersion",
    "org.jetbrains.exposed:exposed-json:$exposedVersion",
    "com.zaxxer:HikariCP:$hikariCpVersion",
    "com.google.code.gson:gson:$gsonVersion",
    "org.yaml:snakeyaml:$snakeYamlVersion",
    "io.github.crackthecodeabhi:kreds:$kredsVersion",
    "com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:$mccourtineVersion",
    "com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:$mccourtineVersion",
)

val runtimeDependencies = listOf(
    "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion",
    "io.insert-koin:koin-core-jvm:$koinVersion",
    "com.github.ajalt.clikt:clikt-jvm:$cliktVersion",
)

spigot {
    authors("ClayClaw")
    commands {
        create("opensky") {
            description = "OpenSky main command"
        }
    }
    libraries = runtimeDependencies + mavenDependencies
    depends("SlimeWorldManager")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:${spigotVersion}")
    compileOnly("com.infernalsuite.aswm:api:1.20.4-R0.1-20240218.183756-10")

    mavenDependencies.forEach(::compileOnly)

    ksp("io.insert-koin:koin-ksp-compiler:$koinAnnotationVersion")
}

sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

val deployPlugin by tasks.registering(Copy::class) {
    dependsOn(tasks.jar)
    System.getenv("PLUGIN_DEPLOY_PATH")?.let {
        from(tasks.jar)
        into(it)
    }
}

val build = (tasks["build"] as Task).apply {
    dependsOn(deployPlugin)
}