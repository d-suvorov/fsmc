package io.github.dsuvorov


class Automaton(val size: Int) {
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