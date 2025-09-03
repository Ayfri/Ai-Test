package entities

import p
import processing.core.PVector

data class Player(
	val pos: PVector = startingPoint.copy(),
	val brain: Brain = Brain()
) {
	val velocity = PVector(0f, 0f)
	var fitness = 0.0
	var hasReachedGoal = false
	var isBest = false
	var touchedWallCount = 0

	fun draw() {
		p.push()
		when {
			isBest -> {
				p.strokeWeight(8f)
				p.stroke(0f, 128f, 255f)
			}

			else -> {
				p.strokeWeight(5f)
				p.stroke(0f)
			}
		}

		p.point(pos.x, pos.y)
		p.pop()
	}

	fun move(level: Level) {
		val pVector = brain.directions[level.step]
		velocity.x += pVector.x
		velocity.y += pVector.y
		velocity.limit(4f)
		pos.add(velocity)
	}

	fun move(level: Level, step: Int) {
		val pVector = brain.directions[step]
		velocity.x += pVector.x
		velocity.y += pVector.y
		velocity.limit(4f)
		pos.add(velocity)
	}

	fun update(level: Level) {
		if (hasReachedGoal) return

		move(level)
		checkCollision(level)
	}

	fun checkCollision(level: Level) {
		if (pos.x < 0 || pos.x > p.width || pos.y < 0 || pos.y > p.height) {
			pos.sub(velocity)

			when {
				pos.x + velocity.x < 0 -> velocity.x = 0f
				pos.x + velocity.x > p.width -> velocity.x = 0f
				pos.y + velocity.y < 0 -> velocity.y = 0f
				pos.y + velocity.y > p.height -> velocity.y = 0f
			}
		}

		level.walls.forEach { wall ->
			if (!wall.collidesWith(this)) return@forEach

			touchedWallCount++
			pos.sub(velocity)

			when {
				pos.x + velocity.x < wall.zone.left -> velocity.x = 0f
				pos.x + velocity.x > wall.zone.right -> velocity.x = 0f
				pos.y + velocity.y < wall.zone.top -> velocity.y = 0f
				pos.y + velocity.y > wall.zone.bottom -> velocity.y = 0f
			}
		}

		if (level.flag.collidesWith(this)) hasReachedGoal = true
	}

	fun createBaby() = Player(brain = brain.clone())

	/**
	 * Calculate fitness based on how close the player is to the goal.
	 * If the player has reached the goal, the fitness is the number of steps it took to reach the goal.
	 */
	fun calculateFitness(level: Level) {
		fitness = if (hasReachedGoal) {
			1.0 / 16.0 + 10000.0 / (level.step * level.step)
//			1.0 / 16.0 + 10000.0 / (level.step * level.step) + 10000.0 / (touchedWallCount * touchedWallCount).coerceAtLeast(1)
		} else {
			val distanceToGoal = pos.dist(level.flag.pos)
			1.0 / +10 / (distanceToGoal * distanceToGoal)
//			1.0 / +10 / (distanceToGoal * distanceToGoal) + 1.0 / (touchedWallCount * touchedWallCount).coerceAtLeast(1)
		}
	}

	override fun toString() = "Player(pos=$pos, velocity=$velocity, fitness=$fitness, hasReachedGoal=$hasReachedGoal, brain=$brain)"

	companion object {
		const val RADIUS = 2f
		const val DIAMETER = RADIUS * 2
		val startingPoint = PVector(p.width / 2f, p.height - 10f)
	}
}
