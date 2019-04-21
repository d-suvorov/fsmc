package io.github.dsuvorov

import io.github.dsuvorov.Automaton.Transition
import java.io.File
import java.util.stream.Collectors

const val INDENT = "  "

fun generateCode(filename: String, automaton: Automaton, definition: ContractDefinition) {
    File(filename).writeText(generateCode(automaton, definition))
}

fun generateCode(automaton: Automaton, definition: ContractDefinition): String {
    val res = StringBuilder()
    val states = Array(automaton.size) { n -> "ST_$n" }

    fun emit(indentLevel: Int, line: String) {
        res.append(INDENT.repeat(indentLevel))
            .append(line)
            .append("\n")
    }

    fun preconditionExpression(transition: List<Transition>) =
        transition.stream()
            .map { t -> "state == States.${states[t.src]}" }
            .collect(Collectors.joining(" || "))

    fun actionNameInternal(name: String) = "_${name}_action"

    fun emitMethod(indentLevel: Int, event: String, transitions: List<Transition>) {
        emit(0, "")
        emit(indentLevel, "function $event() public {")
        val preconditionExpression = preconditionExpression(transitions)
        emit(indentLevel + 1, "require($preconditionExpression)")
        for (t in transitions) {
            emit(indentLevel + 1, "if (state == ${states[t.src]}) {")
            if (t.action.isNotEmpty()) {
                val actionMethod = actionNameInternal(t.action)
                emit(indentLevel + 2, "$actionMethod();")
            }
            emit(indentLevel + 2, "state = ${states[t.dst]};")
            emit(indentLevel + 1, "}")
        }
        emit(indentLevel, "}")
    }

    fun emitAction(indentLevel: Int, action: Action) {
        emit(0, "")
        val actionMethod = actionNameInternal(action.name)
        emit(indentLevel, "function $actionMethod() public {")
        for (line in action.code) {
            emit(indentLevel + 1, line)
        }
        emit(indentLevel, "}")
    }

    val indentLevel = 0
    emit(indentLevel, "contract Contract {")
    emit(indentLevel + 1, "uint private creationTime = now;")
    emit(0, "")
    emit(indentLevel + 1, "enum State {")
    emit(indentLevel + 2, states.toList().stream().collect(Collectors.joining(", ")))
    emit(indentLevel + 1, "}")
    emit(indentLevel + 1, "State private state = States.${states[0]};")

    emit(0, "")
    for (def in definition.definitions) {
        emit(indentLevel + 1, def)
    }

    val eventToTransitions = mutableMapOf<String, MutableList<Transition>>()
    for (transition in automaton.transitions) {
        eventToTransitions.computeIfAbsent(transition.event)
            { mutableListOf() }.add(transition)
    }
    eventToTransitions.forEach { (event, transitions) ->
        emitMethod(indentLevel + 1, event, transitions)
    }

    for (a in definition.actions) {
        emitAction(indentLevel + 1, a)
    }

    emit(indentLevel, "}")

    return res.toString()
}