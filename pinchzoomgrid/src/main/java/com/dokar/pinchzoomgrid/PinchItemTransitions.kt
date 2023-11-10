package com.dokar.pinchzoomgrid

/**
 * Transition types for pinch items.
 */
@JvmInline
value class PinchItemTransitions private constructor(private val value: Int) {
    fun has(other: PinchItemTransitions): Boolean {
        return value and other.value != 0
    }

    operator fun plus(other: PinchItemTransitions): PinchItemTransitions {
        val newValue = value or other.value
        return PinchItemTransitions(newValue)
    }

    operator fun minus(other: PinchItemTransitions): PinchItemTransitions {
        val newValue = value and other.value.inv()
        return PinchItemTransitions(newValue)
    }

    companion object {
        val None = PinchItemTransitions(0)
        val Scale = PinchItemTransitions(1 shl 0)
        val Translate = PinchItemTransitions(1 shl 1)
        val All = Scale + Translate
    }
}
