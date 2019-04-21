package io.github.dsuvorov

import java.util.regex.Pattern


class Automaton(val name: String, val size: Int) {
    class Transition(
        val src: Int,
        val dst: Int,
        val event: String,
        val action: String
    )

    private val _transitions: MutableList<Transition> = mutableListOf()
    val transitions: List<Transition>
        get() = _transitions

    fun addTransition(transition: Transition) {
        if (transition.src >= size || transition.dst >= size)
            throw IllegalArgumentException("transition nodes indices exceed automaton size")
        _transitions.add(transition)
    }
}

fun parseAutomaton(gvText: String): Automaton? {
    val nameExpr = "digraph *(\\w+)"
    val namePattern = Pattern.compile(nameExpr)
    val nameMatcher = namePattern.matcher(gvText)
    nameMatcher.find()
    val contractName = nameMatcher.group(1)

    val edgeExpr = "(\\d+) ?-> ?(\\d+) ?\\[label ?= ?\" ?(\\w+) ?\\[(.+)] \\((.*)\\) ?\"];"
    val edgePattern = Pattern.compile(edgeExpr)
    val edgeMatcher = edgePattern.matcher(gvText)

    var maxLabel = 0
    val srcList = mutableListOf<Int>()
    val dstList = mutableListOf<Int>()
    val eventsList = mutableListOf<String>()
    val actionsList = mutableListOf<String>()

    while (edgeMatcher.find()) {
        val srcLabel = edgeMatcher.group(1).toInt()
        srcList.add(srcLabel)
        val dstLabel = edgeMatcher.group(2).toInt()
        dstList.add(dstLabel)
        maxLabel = maxOf(maxLabel, srcLabel, dstLabel)

        val event = edgeMatcher.group(3)
        if (event.startsWith('_')) {
            System.err.println("Event names must not start with an underscore")
            return null
        }
        eventsList.add(event)
        val action = edgeMatcher.group(5)
        actionsList.add(action)
    }

    val res = Automaton(contractName, maxLabel + 1)
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