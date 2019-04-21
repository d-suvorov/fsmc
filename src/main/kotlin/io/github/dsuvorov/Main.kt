package io.github.dsuvorov

import com.beust.klaxon.Klaxon
import java.io.File
import java.util.regex.Pattern

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
    val automaton = parseAutomaton(gvText)
    val jsonText = File(args[1]).readText()
    val definition = parseContractDefinition(jsonText)!!
    generateCode(args[2], automaton, definition)
}

fun parseAutomaton(gvText: String): Automaton {
    val expr = "(\\d+) ?-> ?(\\d+) ?\\[label ?= ?\" ?(\\w+) ?\\[(.+)] \\((.*)\\) ?\"];"
    val strPattern = Pattern.compile(expr)
    val matcher = strPattern.matcher(gvText)

    var maxLabel = 0
    val srcList = mutableListOf<Int>()
    val dstList = mutableListOf<Int>()
    val eventsList = mutableListOf<String>()
    val actionsList = mutableListOf<String>()

    while (matcher.find()) {
        val srcLabel = matcher.group(1).toInt()
        srcList.add(srcLabel)
        val dstLabel = matcher.group(2).toInt()
        dstList.add(dstLabel)
        maxLabel = maxOf(maxLabel, srcLabel, dstLabel)

        val event = matcher.group(3)
        if (event.startsWith('_')) {
            System.err.println("Event names must not start with an underscore")
        }
        eventsList.add(event)
        val action = matcher.group(5)
        actionsList.add(action)
    }

    val res = Automaton(maxLabel + 1)
    for (i in srcList.indices) {
        val transition = Automaton.Transition(
            src = srcList[i],
            dst = dstList[i],
            event = eventsList[i],
            action = actionsList[i]
        )
        res.addTransition(transition)
    }
    return res
}

data class Action(val name: String, val code: List<String>)
data class ContractDefinition(val definitions: List<String>, val actions: List<Action>)

fun parseContractDefinition(jsonDefinition: String): ContractDefinition? {
    return Klaxon().parse<ContractDefinition>(jsonDefinition)
}
