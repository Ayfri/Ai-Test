package entities

import p

class Population(val level: Level) {
	val isFinished get() = players.any { it.hasReachedGoal || it.brain.step == it.brain.directions.size }
	val players = mutableListOf<Player>()
	val populationSize = 400
	var minSteps = Brain.STARTING_STEPS
	var bestPlayerIndex = 0
	var fitnessSum = 0f
	var generation = 0

	fun draw() {
		if (!level.onlyDisplayBest) players.drop(1).forEach(Player::draw)
		players.first().draw()
	}

	fun update() = players.forEach { it.update(level) }

	fun calculateFitnessSum() = players.sumOf { it.fitness }.toFloat().also { fitnessSum = it }

	fun getBestPlayer(): Player {
		val player = players.maxBy(Player::fitness)
		bestPlayerIndex = players.indexOf(player)
		if (player.hasReachedGoal && player.brain.step < minSteps) minSteps = player.brain.step
		return player
	}

	fun naturalSelection() {
		val newPlayers = ArrayList<Player>(players.size)
		calculateFitnessSum()
		newPlayers += getBestPlayer().createBaby().also { it.isBest = true }

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
		minSteps = Brain.STARTING_STEPS
	}
}
