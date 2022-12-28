import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    id("java-library")
    application
    id("signing")
    id("maven-publish")
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

group = "com.westy92.holidayeventapi"
version = "0.0.1-SNAPSHOT"
description = "The Official Holiday and Event API for Java and Kotlin"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")

    testImplementation(kotlin("test"))
    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.map { it.allSource })
}

val javadocJar by tasks.creating(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
}

application {
    mainClass.set("MainKt")
}

val archives: Configuration by configurations.getting
artifacts {
    add(archives.name, sourcesJar)
    add(archives.name, javadocJar)
}

signing {
    sign(configurations.archives.name)
}

publishing {
    repositories {
        maven {
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            name = "deploy"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            /*credentials {
                username = java.lang.System.getenv("OSSRH_USERNAME") ?: ""
                password = java.lang.System.getenv("OSSRH_PASSWORD") ?: ""
            }*/
        }
    }

    publications.withType<MavenPublication>().forEach {
        it.apply {
            artifact(sourcesJar)
            artifact(javadocJar)
            pom {
                name.set("Holiday and Event API")
                description.set(project.description)
                inceptionYear.set("2022")
                url.set("https://github.com/westy92/holiday-event-api-java")
                developers {
                    developer {
                        name.set("Seth Westphal")
                        id.set("westy92")
                        email.set("seth@sethwestphal.com")
                    }
                }
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/westy92/holiday-event-api-java.git")
                    developerConnection.set("scm:git:https://github.com/westy92/holiday-event-api-java.git")
                    url.set("https://github.com/westy92/holiday-event-api-java")
                }
            }
        }
    }
}
