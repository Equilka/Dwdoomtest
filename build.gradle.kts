plugins {
    application
    id("com.gradleup.shadow") version "9.0.0-beta12"
    id("java")
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(25) }
}

tasks.jar {
    manifest { attributes["Main-Class"] = "com.equilka.discordbot.Main" }
}

application.mainClass = "com.equilka.discordbot.Dwdoomtest"
group = "org.equilka"
version = "1.0"

val jdaVersion = "6.5.0"
val jdaUtilsVersion = "2.1"

repositories {
    mavenCentral()

    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }

    maven { url = uri("https://m2.chew.pro/releases") }

    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("net.dv8tion:JDA:$jdaVersion")
    implementation("pw.chew:jda-chewtils:${jdaUtilsVersion}")
    implementation("club.minnced:jdave-api:0.1.8")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("io.github.cdimascio:dotenv-java:3.2.0")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("com.github.walkyst:lavaplayer-fork:1.4.3")
    implementation("org.junit.jupiter:junit-jupiter:5.12.2")
    implementation("org.mockito:mockito-core:5.17.0")

    implementation("club.minnced:jdave-native-linux-x86-64:0.1.8")
    implementation("club.minnced:jdave-native-linux-aarch64:0.1.8")
    implementation("club.minnced:jdave-native-win-x86-64:0.1.8")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.isIncremental = true

    sourceCompatibility = "25"
}