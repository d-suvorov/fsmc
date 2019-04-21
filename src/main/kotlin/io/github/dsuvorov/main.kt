package io.github.dsuvorov

import java.io.File

fun main(args: Array<String>) {
    if (args.size != 3) {
        val usage = """
        |Usage: fsmc <fsm> <definition> <output>
        | <fsm>        - a file with FSM model in dot language
        | <definition> - a JSON file containing contract fields and actions definitions
        | <output>     - output file
        """.trimMargin()
        println(usage)
        System.exit(0)
    }

    val gvText = File(args[0]).readText()
    val automaton = parseAutomaton(gvText) ?: return
    val jsonText = File(args[1]).readText()
    val definition = parseContractDefinition(jsonText)
    if (definition == null) {
        System.err.println("An error occurred during contract description parsing")
        return
    }
    generateCode(args[2], automaton, definition)
}