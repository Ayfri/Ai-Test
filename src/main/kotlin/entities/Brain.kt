package entities

import fastRandom
import fastRandomVec

class Brain(steps: Int = STARTING_STEPS) {
	var directions = MutableList(steps) { fastRandomVec() }
		private set

	fun clone(): Brain {
		val clone = Brain(0)
		clone.directions = directions.toMutableList()
		return clone
	}

	fun mutate() {
		for (i in directions.indices) {
			if (fastRandom.nextDouble() < mutationRate) directions[i] = fastRandomVec()
		}
	}

	override fun toString() = "Brain(directions=$directions)"

	companion object {
		const val STARTING_STEPS = 10000
		var mutationRate = 0.05f
			set(value) {
				if (value < 0 || value > 1) return
				field = value
			}
	}
}
