import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply(plugin = "maven-publish")
apply(plugin = "signing")

plugins {
    kotlin("jvm") version "2.0.21"
    id("java-library")
    application
    id("signing")
    id("maven-publish")
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "com.westy92.holiday-event-api"
version = "0.0.2"
description = "The Official Holiday and Event API for Java and Kotlin"

val ossrhUsername: String? by project
val ossrhPassword: String? by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")

    testImplementation(kotlin("test"))
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.map { it.allSource })
}

val javadocJar by tasks.creating(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
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
    val extension = extensions.getByName("publishing") as PublishingExtension
    sign(extension.publications)
}

publishing {
    repositories {
        maven {
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            name = "deploy"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }

    publications.create<MavenPublication>("mavenJava") {
        artifact(sourcesJar)
        artifact(javadocJar)
        pom {
            val projectGitUrl = "https://github.com/westy92/holiday-event-api-java"
            name.set("Holiday and Event API")
            description.set(project.description)
            inceptionYear.set("2022")
            url.set(projectGitUrl)
            developers {
                developer {
                    name.set("Seth Westphal")
                    id.set("westy92")
                    url.set("https://github.com/westy92")
                }
            }
            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            issueManagement {
                system.set("GitHub")
                url.set("$projectGitUrl/issues")
            }
            scm {
                connection.set("scm:git:$projectGitUrl.git")
                developerConnection.set("scm:git:$projectGitUrl.git")
                url.set(projectGitUrl)
            }
        }
    }
}
