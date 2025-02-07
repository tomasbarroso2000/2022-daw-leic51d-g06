import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.4"
    id("io.spring.dependency-management") version "1.0.14.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    java
}

group = "pt.isel.leic.daw.explodingbattleships"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val isArm = System.getProperty("os.arch") == "aarch64"
val isMac = System.getProperty("os.name").toLowerCase().contains("mac")

repositories {
    mavenCentral()
}

val ktlint by configurations.creating

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.security:spring-security-core:5.7.3")

    // for JDBI
    implementation("org.jdbi:jdbi3-core:3.34.0")
    implementation("org.jdbi:jdbi3-kotlin:3.34.0")
    implementation("org.jdbi:jdbi3-postgres:3.34.0")
    implementation("org.postgresql:postgresql:42.5.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")

    // TODO may need to consider other environments as well
    if(isMac && isArm) {
        runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.82.Final:osx-aarch_64")
    }
    testImplementation(kotlin("test"))

    ktlint("com.pinterest:ktlint:0.47.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    environment(
        mapOf("POSTGRES_URI" to "jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
    )
}

task<Copy>("extractUberJar") {
    dependsOn("assemble")
    // opens the JAR containing everything...
    from(zipTree("$buildDir/libs/${rootProject.name}-$version.jar"))
    // ... into the 'build/dependency' folder
    into("build/dependency")
}

task<Exec>("dbTestsUp") {
    commandLine("docker-compose", "up", "-d", "--build", "--force-recreate", "db-tests")
}

task<Exec>("dbTestsWait") {
    commandLine("docker", "exec", "db-tests", "/app/bin/wait-for-postgres.sh", "localhost")
    dependsOn("dbTestsUp")
}

task<Exec>("dbTestsDown") {
    commandLine("docker-compose", "down")
}

task<Exec>("composeUp") {
    commandLine("docker-compose", "up", "--build", "--force-recreate")
    dependsOn("extractUberJar")
}

// from https://pinterest.github.io/ktlint/install/integrations/#custom-gradle-integration-with-kotlin-dsl
val outputDir = "${project.buildDir}/reports/ktlint/"
val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))

val ktlintCheck by tasks.creating(JavaExec::class) {
    inputs.files(inputFiles)
    outputs.dir(outputDir)

    description = "Check Kotlin code style."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    // see https://pinterest.github.io/ktlint/install/cli/#command-line-usage for more information
    args = listOf("src/**/*.kt")
}

tasks.named("check") {
    dependsOn(ktlintCheck)
    dependsOn("dbTestsWait")
    finalizedBy("dbTestsDown")
}