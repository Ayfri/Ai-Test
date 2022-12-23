package entities

import p
import processing.core.PVector

class Brain(steps: Int = STARTING_STEPS) {
	val directions = MutableList(steps) { PVector.random2D() }
	var step = 0

	fun clone(): Brain {
		val clone = Brain(0)
		clone.directions.addAll(directions)
		return clone
	}

	fun mutate() {
		for (i in 0 until directions.size) {
			if (p.random(1f) < mutationRate) directions[i] = PVector.random2D()
		}
	}

	override fun toString() = "Brain(directions=$directions, step=$step)"

	companion object {
		const val STARTING_STEPS = 5000
		var mutationRate = 0.05f
			set(value) {
				if (value < 0 || value > 1) return
				field = value
			}
	}
}
