import org.gradle.kotlin.dsl.implementation

plugins {
    id("java")
    id("org.springframework.boot") version "4.0.3"
    id("io.freefair.lombok") version "9.2.0"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-resttestclient")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("com.google.code.gson:gson:2.9.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.google.guava:guava:33.2.1-jre")
}

tasks.test {
    useJUnitPlatform()
}