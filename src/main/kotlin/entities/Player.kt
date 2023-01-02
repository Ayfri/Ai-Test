package entities

import collisions
import line
import p
import processing.core.PVector
import kotlin.math.round

data class Circle(val pos: PVector, val radius: Float) {
	val diameter get() = radius * 2f

	fun intersects(zone: Zone): Boolean {
		var testX = pos.x
		var testY = pos.y

		if (pos.x < zone.left) testX = zone.left
		else if (pos.x > zone.right) testX = zone.right
		if (pos.y < zone.top) testY = zone.top
		else if (pos.y > zone.bottom) testY = zone.bottom

		val distX = pos.x - testX
		val distY = pos.y - testY
		val distance = kotlin.math.sqrt((distX * distX) + (distY * distY))

		return distance <= radius
	}
}

data class Player(val pos: PVector = startingPoint.copy(), val brain: Brain = Brain()) {
	val velocity = PVector(0f, 0f)

	// I'm bad at maths, so zones are broken for now.
	// val zone = Circle(pos, RADIUS)
	var fitness = 0.0
	var hasReachedGoal = false
	var isBest = false

	fun draw() {
		p.pushMatrix()
		when {
			isBest -> {
				p.strokeWeight(8f)
				p.stroke(0f, 128f, 255f)
				line += pos.copy()
			}

			else -> {
				p.strokeWeight(5f)
				p.stroke(0f)
			}
		}

		p.translate(pos.x, pos.y)
		p.point(0f, 0f)
		p.strokeWeight(0.75f)
		p.stroke(0f)
		p.popMatrix()
	}

	fun move() {
		if (!hasReachedGoal) {
			velocity.add(brain.directions[brain.step])
			brain.step++
		}

		velocity.limit(6f)
		pos.add(velocity)
	}

	fun update(level: Level) {
		if (!hasReachedGoal) {
			move()
			checkCollision(level)
		}
	}

	fun collidesWith(point: PVector) = point.x > pos.x - RADIUS && point.x < pos.x + RADIUS && point.y > pos.y - RADIUS && point.y < pos.y + RADIUS

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

			collisions += pos.copy().apply {
				x = round(x)
				y = round(y)
			}

			pos.sub(velocity)

			when {
				pos.x + velocity.x < wall.zone.left -> velocity.x = 0f
				pos.x + velocity.x > wall.zone.right -> velocity.x = 0f
				pos.y + velocity.y < wall.zone.top -> velocity.y = 0f
				pos.y + velocity.y > wall.zone.bottom -> velocity.y = 0f
			}
		}

		if (pos in level.flag) hasReachedGoal = true
	}

	fun createBaby() = Player(brain = brain.clone())

	/**
	 * Calculate fitness based on how close the player is to the goal.
	 * If the player has reached the goal, the fitness is the number of steps it took to reach the goal.
	 */
	fun calculateFitness(level: Level) {
		fitness = if (hasReachedGoal) {
			1.0 / 16.0 + 10000.0 / (brain.step * brain.step)
		} else {
			val distanceToGoal = pos.dist(level.flag.pos)
			1.0 / +10 / (distanceToGoal * distanceToGoal)
		}
	}

	override fun toString() = "Player(pos=$pos, velocity=$velocity, fitness=$fitness, hasReachedGoal=$hasReachedGoal, brain=$brain)"

	companion object {
		const val RADIUS = 2f
		const val DIAMETER = RADIUS * 2
		val startingPoint = PVector(p.width / 2f, p.height - 10f)
	}
}
