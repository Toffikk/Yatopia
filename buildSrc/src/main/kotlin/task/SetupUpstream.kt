package task

import bashCmd
import gitHash
import lastUpstream
import org.gradle.api.Project
import org.gradle.api.Task
import taskGroup
import toothpick
import upstreamDir

internal fun Project.createSetupUpstreamTask(
    receiver: Task.() -> Unit = {}
): Task = tasks.create("setupUpstream") {
    receiver(this)
    group = taskGroup
    doLast {
        val setupUpstreamCommand = if (upstreamDir.resolve("scripts/build.sh").exists()) {
            "scripts/build.sh || exit 1"
        } else if (
                upstreamDir.resolve("build.gradle.kts").exists()
                && upstreamDir.resolve("subprojects/server.gradle.kts").exists()
                && upstreamDir.resolve("subprojects/api.gradle.kts").exists()
        ) {
            if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                "gradlew applyPatches"
            } else {
                "./gradlew applyPatches"
            }
        } else if (
                upstreamDir.resolve("build.gradle.kts").exists()
        ) {
            if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                "gradlew clean build"
            } else {
                "./gradlew clean build"
            }
        } else if (
                upstreamDir.resolve("build.gradle").exists()
        ) {
            if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                "gradlew clean build"
            } else {
                "./gradlew clean build"
            } else {
                error("Can't patch upstream! , please check if upstream's build tool is supported , " +
                        "Supported Tools: Maven , Gradle")
            }
            val result = bashCmd(setupUpstreamCommand, dir = upstreamDir, printOut = true)
            if (result.exitCode != 0) {
                error("Failed to apply upstream patches: script exited with code ${result.exitCode}")
            }
            lastUpstream.writeText(gitHash(upstreamDir))
        }
    }
}