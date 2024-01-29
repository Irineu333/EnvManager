package com.neo.envmanager.command

import com.github.ajalt.clikt.parameters.arguments.argument
import com.neo.envmanager.com.neo.envmanager.util.extension.update
import com.neo.envmanager.core.Command
import com.neo.envmanager.model.Environment
import com.neo.envmanager.util.extension.requireInstall

class Rename : Command(
    help = "Rename an environment"
) {

    private val tag by argument(
        help = "Environment tag"
    )

    private val newTag by argument(
        help = "New environment tag"
    )

    override fun run() {

        val config = requireInstall()

        Environment.get(
            paths.environmentsDir,
            tag
        ).renameTo(newTag)

        if (tag == config.currentEnv) {
            config.update {
                it.copy(
                    currentEnv = newTag
                )
            }
        }
    }
}