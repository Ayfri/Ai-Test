package entities

import p

class Population(val level: Level, val players: MutableList<Player> = mutableListOf()) {
	val fitnessSum get() = players.sumOf { it.fitness }.toFloat()
	val isFinished get() = players.any { it.hasReachedGoal || it.brain.step == it.brain.directions.size }

	var generation = 0
	var minSteps = Brain.STARTING_STEPS

	fun getBestPlayer(): Player {
		val player = players.maxBy(Player::fitness)
		if (player.hasReachedGoal && player.brain.step < minSteps) minSteps = player.brain.step
		return player
	}

	fun naturalSelection() {
		val newPlayers = ArrayList<Player>(players.size)
		calculateFitness()

		newPlayers.add(getBestPlayer().createBaby())

		for (i in 1 until players.size) {
			val parent = selectParent()
			newPlayers += parent.createBaby()
		}

		players.clear()
		players.addAll(newPlayers)
		generation++
	}

	fun selectParent(): Player {
		val rand = p.random(fitnessSum)
		var runningSum = 0.0

		return players.first {
			runningSum += it.fitness
			runningSum > rand
		}
	}

	fun calculateFitness() = players.forEach { it.calculateFitness(level) }

	fun mutatePlayers() = players.forEach { it.brain.mutate() }

	fun reset() {
		generation = 0
		minSteps = Brain.STARTING_STEPS
	}
}
