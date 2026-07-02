plugins {
    kotlin("jvm") version "2.3.21"
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("ServerKt")
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.oshi:oshi-core:7.3.2")
    implementation("org.slf4j:slf4j-simple:2.0.18")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}
