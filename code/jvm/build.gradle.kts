import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

group = "pt.isel.leic.daw.explodinbattleships"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "org.postgresql", name = "postgresql", version = "42.+")
    implementation("org.jdbi:jdbi3-kotlin:3.32.0")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("net.glxn:qrgen:1.4")
    implementation("junit:junit:4.13.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.springframework.security:spring-security-core:5.7.3")

    // for JDBI
    implementation("org.jdbi:jdbi3-core:3.33.0")
    implementation("org.jdbi:jdbi3-kotlin:3.33.0")
    implementation("org.jdbi:jdbi3-postgres:3.33.0")
    implementation("org.postgresql:postgresql:42.5.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
