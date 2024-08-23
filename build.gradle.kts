plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.0.20"
    id("com.google.protobuf") version "0.9.4"
}

group = "no.mattilsynet.sample.external.nats"

if (project.hasProperty("releaseVersion")) {
    version = project.properties["releaseVersion"]!!
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

//NB! com.google.cloud:spring-cloud-gcp-dependencies:5.x.x støtter ikke protobuf 4.x.x vent til oppdatering
val protobuf = "3.25.3"

dependencies {
    implementation("com.google.protobuf:protobuf-kotlin:$protobuf")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobuf"
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("kotlin")

            }
        }
    }
}

publishing {
    repositories {
        maven {
            name = "github"
            url = uri("https://maven.pkg.github.com/Mattilsynet/provetaking-protobuf")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register("gpr", MavenPublication::class) {
            from(components["java"])
        }
    }
}

kotlin {
    compilerOptions.jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
    compilerOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
}

sourceSets {
    main {
        proto {
            srcDir("proto")
        }
    }
}