package entities

import fastRandom

class Population(val level: Level) {
	val isFinished get() = players.any { it.hasReachedGoal || level.step >= level.minSteps }
	val populationSize = 500
	val players = Array(populationSize) { Player() }
	var bestPlayerIndex = 0
	var fitnessSum = 0.0
	var generation = 0
		set(value) {
			field = value
			//		averageCollisionCalculationTime.trimToSize()
		}

	fun draw() {
		if (!level.onlyDisplayBest) {
			players.drop(1).forEach(Player::draw)
		}
		players[0].draw()
	}

	fun update() = players.forEach {
//		averageCollisionCalculationTime += measureNanoTime {
		it.update(level)
//		}
	}

	fun calculateFitnessSum() = players.sumOf { it.fitness }.also { fitnessSum = it }

	fun getBestPlayer(): Player {
		val player = players.maxBy(Player::fitness)
		bestPlayerIndex = players.indexOf(player)
		return player
	}

	fun naturalSelection() {
		val newPlayers = ArrayList<Player>(players.size)
		calculateFitnessSum()

		newPlayers += getBestPlayer().createBaby().apply { isBest = true }

		if (level.step < level.minSteps) level.minSteps = level.step

		for (i in 1..<players.size) {
			val parent = selectParent()
			newPlayers += parent.createBaby()
		}

		newPlayers.forEachIndexed { index, player -> players[index] = player }
		generation++
	}

	fun selectParent(): Player {
		val rand = fastRandom.nextDouble(fitnessSum)
		var runningSum = 0.0

		return players.first {
			runningSum += it.fitness
			runningSum > rand
		}
	}

	fun setPlayers() {
		players.forEachIndexed { index, _ -> players[index] = Player() }
	}

	fun calculateFitness() = players.forEach { it.calculateFitness(level) }

	fun mutatePlayers() = players.drop(1).forEach { it.brain.mutate() }

	fun reset() {
		setPlayers()
		generation = 0
	}
}
