package com.neo.properties.commands

import com.github.ajalt.clikt.core.Abort
import com.github.ajalt.clikt.core.terminal
import com.google.gson.Gson
import com.neo.properties.core.Command
import com.neo.properties.model.Config
import com.neo.properties.util.extension.readAsProperties
import com.neo.properties.util.Constants
import com.neo.properties.util.Instructions
import com.neo.properties.util.extension.promptFile
import java.io.File

/**
 * Install environment control
 * @author Irineu A. Silva
 */
class Install : Command(help = "Install environment control") {

    override fun run() {

        if (isInstalled()) {
            echo("✔ Already installed")
            // TODO: Add option to view and change config
            throw Abort()
        }

        installDir.mkdir()

        createGitIgnore()

        finished(createConfig())
    }

    private fun finished(config: Config) {

        echo("\n✔ Installation complete")

        val properties = File(
            config.targetPath
        ).readAsProperties()

        if (properties.isNotEmpty()) {
            echo("\n! ${properties.count()} properties found.")
            echo(Instructions.SAVE)
        }
    }

    private fun createConfig(): Config {

        val target = terminal.promptFile(
            text = "Environment properties file",
            mustExist = true,
            canBeDir = false,
        )

        return Config(
            targetPath = target.path
        ).also {
            configFile.writeText(
                Gson().toJson(it)
            )
        }
    }

    private fun createGitIgnore() = with(
        installDir.resolve(Constants.DOT_GITIGNORE)
    ) {
        writeText("*")
    }
}
