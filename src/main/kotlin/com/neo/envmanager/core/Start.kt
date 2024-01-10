package com.neo.envmanager.core

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.neo.envmanager.command.*
import com.neo.envmanager.model.Paths
import java.io.File

class Start : CliktCommand(
    name = "envm",
    invokeWithoutSubcommand = true,
    printHelpOnEmptyArgs = true
) {

    private val version by option(
        names = arrayOf("-v", "--version"),
        help = "Show version"
    ).flag()

    private val project by option(
        help = "Project path"
    ).file(
        mustExist = true,
        canBeDir = true,
        canBeFile = false
    ).default(File("."))

    init {
        subcommands(
            Install(),
            Save(),
            Lister(),
            Checkout(),
            Remove()
        )
    }

    override fun run() {

        if (version) throw PrintCompletionMessage("1.0-DEV")

        currentContext.obj = Paths(
            projectDir = project
        )
    }
}