plugins {
    id("java")
    id("application")
}

repositories {
	maven {
		name = "Fabric"
		url = uri("https://maven.fabricmc.net/")
	}
mavenCentral()
}

dependencies {
    // This dependency is used by the application.
    implementation("com.google.guava:guava:30.1.1-jre")

    // srg management
    implementation("org.cadixdev:lorenz:0.5.7")

    // source code remapping
    implementation("org.cadixdev:mercury:0.1.0.fabric-SNAPSHOT")
}

application {
    mainClass.set("mapper.Main")
}
