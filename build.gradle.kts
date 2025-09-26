plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.fensy.dev"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

val thumbnailatorVersion = "0.4.20"
val ffmpegWrapperVersion = "0.8.0"
dependencies {
	implementation("com.graphql-java:graphql-java-extended-scalars:20.1")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    //jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.oshai:kotlin-logging:7.0.0")


    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // database
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework:spring-jdbc")
    implementation("org.liquibase:liquibase-core")
    implementation("org.postgresql:r2dbc-postgresql")
    runtimeOnly("org.postgresql:postgresql")

    // graphql
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("com.graphql-java:graphql-java-extended-scalars:21.0")

    // security
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security:3.4.5")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.5.0")

    // aws
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:3.1.1"))
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3")
    implementation("software.amazon.awssdk:netty-nio-client")

    // ton
//    implementation("io.github.neodix42:smartcontract:1.0.0")
    implementation("org.ton:ton-kotlin:0.2.18")

//    implementation("org.ton:tonconnect-sdk:0.1.0")

    // image and video compression
    implementation("com.github.kokorin.jaffree:jaffree:2024.08.29")
    implementation("net.coobird:thumbnailator:$thumbnailatorVersion")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
