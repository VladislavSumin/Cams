import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        jcenter()

    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.3.50"))
    }
}

repositories {
    mavenCentral()
    jcenter()
}


plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.3.50"
    id("org.springframework.boot") version "2.1.8.RELEASE"

    /**
     * Check plugin && library versions
     * Use task "dependencyUpdates" to check updates
     */
    id("com.github.ben-manes.versions") version ("0.25.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

apply {
    plugin("io.spring.dependency-management")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(files("./../../NioSocketListener/build/libs/NioSocketListener.jar"))

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.10")

    //Database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtime("com.h2database:h2:1.4.199")

    //Gson
    implementation("com.google.code.gson:gson:2.8.5")

    //FFMPEG
    implementation("net.bramp.ffmpeg:ffmpeg:0.6.2")

    testCompile("junit", "junit", "4.12")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}


tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    resolutionStrategy {
        componentSelection {
            all {
                // Disable alpha/beta library versions
                val rejected = listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea", "pr").any { qualifier ->
                    candidate.version.matches(Regex("(?i).*[.-]$qualifier[.\\d-+]*"))
                }
                if (rejected) reject("Release candidate")
            }
        }
    }
    checkForGradleUpdate = true
}