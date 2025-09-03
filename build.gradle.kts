plugins {
	kotlin("jvm") version "2.2.10"
    application
}

group = "fr.ayfri"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
	maven {
		name = "JogAmp"
		url = uri("https://jogamp.org/deployment/maven")
	}
}

dependencies {
	implementation("org.processing:core:4.4.4")
}

kotlin {
	jvmToolchain(21)
}

application {
	mainClass = "SimpleReachGameKt"
}
