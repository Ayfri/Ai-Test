package entities

import fastRandom

class Population(val level: Level) {
	val isFinished get() = players.any { it.hasReachedGoal || level.step >= level.minSteps }
	val players = mutableListOf<Player>()
	val populationSize = 500
	var bestPlayerIndex = 0
	var fitnessSum = 0.0
	var generation = 0

	fun draw() {
		if (!level.onlyDisplayBest) players.drop(1).forEach(Player::draw)
		players.first().draw()
	}

	fun update() = players.forEach { it.update(level) }

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

		for (i in 1 until players.size) {
			val parent = selectParent()
			newPlayers += parent.createBaby()
		}

		players.clear()
		players.addAll(newPlayers)
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
		players.clear()
		for (i in 0 until populationSize) {
			players += Player()
		}
	}

	fun calculateFitness() = players.forEach { it.calculateFitness(level) }

	fun mutatePlayers() = players.subList(1, players.size).forEach { it.brain.mutate() }

	fun reset() {
		setPlayers()
		generation = 0
	}
}
