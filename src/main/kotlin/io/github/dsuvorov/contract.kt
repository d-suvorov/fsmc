package io.github.dsuvorov

import com.beust.klaxon.Klaxon

data class Action(val name: String, val code: List<String>)
data class ContractDefinition(val definitions: List<String>, val actions: List<Action>)

fun parseContractDefinition(jsonDefinition: String): ContractDefinition? {
    return Klaxon().parse<ContractDefinition>(jsonDefinition)
}