plugins {
    bluemap.java
    id("com.gradleup.shadow")
}

group = "me.owies"
version = "0.5.0-java21"

repositories {
    // BlueMapModelLoaders upstream publishes/resolves BMUtils through this repository.
    // BlueMap's root repository declaration filters it to de.bluecolored.*, so the
    // addon needs an unfiltered declaration here for com.technicjelle:BMUtils.
    maven("https://repo.bluecolored.de/releases")
}

dependencies {
    implementation("com.technicjelle:BMUtils:4.3.1")
    implementation(libs.caffeine)

    compileOnly(project(":common"))
    compileOnly(project(":core"))
    compileOnly("de.bluecolored:bluemap-api")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks.shadowJar {
    relocate("com.technicjelle.BMUtils", "${project.group}.${project.name}.BMUtils")
    relocate("com.github.benmanes.caffeine", "${project.group}.${project.name}.caffeine")
    relocate("org.checkerframework", "${project.group}.${project.name}.checkerframework")
    relocate("com.google.errorprone", "${project.group}.${project.name}.errorprone")
    relocate("org.intellij", "${project.group}.${project.name}.intellij")
    relocate("org.jetbrains", "${project.group}.${project.name}.jetbrains")
    archiveFileName = "BlueMapModelLoaders-${project.version}.jar"
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    filesMatching("bluemap.addon.json") {
        expand("version" to project.version)
    }
}
