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
		// Check boundaries collision
		val nextX = pos.x + velocity.x
		val nextY = pos.y + velocity.y

		if (nextX < RADIUS || nextX > p.width - RADIUS || nextY < RADIUS || nextY > p.height - RADIUS) {
			pos.sub(velocity)

			when {
				nextX < RADIUS -> velocity.x = 0f
				nextX > p.width - RADIUS -> velocity.x = 0f
				nextY < RADIUS -> velocity.y = 0f
				nextY > p.height - RADIUS -> velocity.y = 0f
			}
		}

		// Check wall collisions - prevent penetration while allowing sliding
		level.walls.forEach { wall ->
			// Check if player would collide with wall after full movement
			if (!wall.collidesWith(nextX, nextY, RADIUS)) return@forEach

			touchedWallCount++

			// Test movement in each axis separately to allow sliding
			val canMoveX = !wall.collidesWith(pos.x + velocity.x, pos.y, RADIUS)
			val canMoveY = !wall.collidesWith(pos.x, pos.y + velocity.y, RADIUS)

			// If player is already overlapping with wall, push them out
			if (wall.collidesWith(pos.x, pos.y, RADIUS)) {
				// Calculate push-out direction based on wall center
				val wallCenterX = wall.zone.pos.x
				val wallCenterY = wall.zone.pos.y
				val relativeX = pos.x - wallCenterX
				val relativeY = pos.y - wallCenterY

				// Calculate minimum distances to push out of wall
				val xDistanceToEdge = wall.zone.halfWidth + RADIUS - kotlin.math.abs(relativeX)
				val yDistanceToEdge = wall.zone.halfHeight + RADIUS - kotlin.math.abs(relativeY)

				// Push out in the direction that requires minimum movement
				if (xDistanceToEdge < yDistanceToEdge) {
					// Push out horizontally
					pos.x = wallCenterX + (wall.zone.halfWidth + RADIUS) * kotlin.math.sign(relativeX)
					velocity.x = 0f
				} else {
					// Push out vertically
					pos.y = wallCenterY + (wall.zone.halfHeight + RADIUS) * kotlin.math.sign(relativeY)
					velocity.y = 0f
				}
			} else {
				// Player is approaching wall - block movement in directions that would cause collision
				if (!canMoveX) {
					velocity.x = 0f
				}
				if (!canMoveY) {
					velocity.y = 0f
				}
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
