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
                "scripts/build.sh"
        } else if (
                upstreamDir.resolve("scripts/apply.sh").exists()
        ) {
            "scripts/importmcdev.sh"
            "scripts/apply.sh"
        } else if (
                upstreamDir.resolve("scripts/applyPatches.sh").exists()
        ) {
            "scripts/generateImports.sh"
            "scripts/importSources.sh"
            "scripts/applyPatches.sh"
        } else if (
                upstreamDir.resolve("build.gradle.kts").exists()
                && upstreamDir.resolve("subprojects/server.gradle.kts").exists()
                && upstreamDir.resolve("subprojects/api.gradle.kts").exists()
        ) {
            if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                "gradlew importMCDev"
                "gradlew applyPatches"
            } else {
                "./gradlew importMCDev"
                "./gradlew applyPatches"
                }
        } else {
            error("Don't know how to setup upstream!")
        }
        val result = bashCmd(setupUpstreamCommand, dir = upstreamDir, printOut = true)
        if (result.exitCode != 0) {
            error("Failed to apply upstream patches: script exited with code ${result.exitCode}")
        }
        lastUpstream.writeText(gitHash(upstreamDir))
    }
}