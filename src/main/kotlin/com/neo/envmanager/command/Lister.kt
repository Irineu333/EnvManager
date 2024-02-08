package com.neo.envmanager.command

import com.github.ajalt.clikt.core.Abort
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.widgets.Text
import com.neo.envmanager.com.neo.envmanager.exception.error.NoCurrentEnvironment
import com.neo.envmanager.exception.error.EnvironmentNotFound
import com.neo.envmanager.exception.error.NoEnvironmentsFound
import com.neo.envmanager.model.Environment
import com.neo.envmanager.model.Installation
import com.neo.envmanager.model.Target
import com.neo.envmanager.util.Constants
import com.neo.envmanager.util.Instructions
import com.neo.envmanager.util.extension.requireInstall
import com.neo.envmanager.util.extension.tag
import extension.getOrElse

class Lister : CliktCommand(
    name = "list",
    help = "List environments or properties"
) {
    private val tag by tag().optional()

    private val current by option(
        names = arrayOf("-c", "--current"),
        help = "Show current environment"
    ).flag()

    private val target by option(
        names = arrayOf("-t", "--target"),
        help = "Show target properties"
    ).flag()

    private lateinit var installation: Installation

    override fun run() {

        installation = requireInstall()

        if (target) {
            showTarget()
            return
        }

        if (current) {

            val tag = installation.config.currentEnv ?: throw NoCurrentEnvironment()

            echo(terminal.theme.info(text = "> $tag"))

            showEnvironmentByTag(tag)

            return
        }

        if (tag != null) {

            showEnvironmentByTag(tag!!)

            return
        }

        showAllEnvironments()
    }

    private fun showTarget() {

        val target = Target(installation.config.targetFile)

        target.read().forEach { (key, value) ->

            val property = "$key" +
                    Constants.PROPERTY_SEPARATOR +
                    TextStyles.dim("$value")

            terminal.println(Text(property))
        }
    }

    private fun showEnvironmentByTag(tag: String) {

        val environment = Environment.getSafe(
            dir = installation.environmentsDir,
            tag = tag
        ).getOrElse {

            echoFormattedHelp(EnvironmentNotFound(tag))
            echo(Instructions.SAVE)

            throw Abort()
        }

        environment.read().forEach { (key, value) ->

            val property = key +
                    Constants.PROPERTY_SEPARATOR +
                    TextStyles.dim(value)

            terminal.println(Text(property))
        }
    }

    private fun showAllEnvironments() {

        val environments = installation.environmentsDir.listFiles { _, name ->
            name.endsWith(Constants.DOT_JSON)
        }

        if (environments.isNullOrEmpty()) {

            echoFormattedHelp(NoEnvironmentsFound())
            echo(Instructions.SAVE)

            throw Abort()
        }

        environments.forEach { environmentFile ->

            val environment = Environment.getSafe(
                environmentFile
            ).getOrElse {
                return@forEach
            }

            val isCurrentEnvironment = environment.tag == installation.config.currentEnv

            val target = Target.getSafe(
                installation.config.targetFile
            ).getOrElse { null }

            echo(
                Text(
                    when {
                        target == null -> {
                            environment.tag
                        }

                        isCurrentEnvironment && environment.isCurrent(target) -> {
                            TextStyles.bold(environment.tag)
                        }

                        isCurrentEnvironment -> {
                            TextStyles.bold(environment.tag.plus("*"))
                        }

                        else -> {
                            environment.tag
                        }
                    }
                )
            )
        }
    }
}

fun Environment.isCurrent(
    target: Target
): Boolean {

    return read() == target.read().toMap()
}
