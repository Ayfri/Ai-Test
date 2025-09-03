package entities

import fastRandom
import fastRandomVec


class Brain(steps: Int = STARTING_STEPS) {
	private val randomGenerator = fastRandom.split()

	var directions = moves(steps) {
		randomGenerator.nextInt(0, Moves.MAX_VALUE)
	}

	fun clone(): Brain {
		val clone = Brain(0)
		clone.directions = directions.clone()
		return clone
	}

	fun mutate() {
		val quantityOfMutations = (directions.size * mutationRate).toLong()
		val indicesToChange = fastRandom.ints(0, directions.size).distinct().limit(quantityOfMutations).toArray()

		indicesToChange.forEach { directions[it] = fastRandomVec() }
	}

	override fun toString() = "Brain(directions=$directions)"

	companion object {
		const val STARTING_STEPS = 10000
		var mutationRate = 0.05f
			set(value) {
				if (value !in 0.0..1.0) return
				field = value
			}
	}
}
