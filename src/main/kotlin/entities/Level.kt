package entities

import p
import processing.core.PApplet
import processing.core.PVector

class Level {
	val walls = mutableListOf<Wall>()
	val flag = Flag(PVector())
	val population = Population(this)
	var onlyDisplayBest = false
	var step = 0
	var minSteps = Brain.STARTING_STEPS

	fun draw() {
		flag.draw()
		population.draw()
	}

	fun update() {
		step++
		population.update()
	}

	fun generateRandomWalls(count: Int) {
		walls.clear()
		while (walls.size < count) {
			val x = p.random(p.width.toFloat())
			val y = p.random(p.height.toFloat())
			val height = p.random(Wall.heightRange.start, Wall.heightRange.endInclusive)
			val width = p.random(Wall.widthRange.start, Wall.widthRange.endInclusive)
			val angle = p.random(PApplet.TWO_PI)

			val wall = Wall(PVector(x, y), height, width, angle)
			if (wall.collidesWith(p.width / 2f, 10f, flag.radius)) continue
			if (wall.collidesWith(Player.startingPoint.x, Player.startingPoint.y, Player.RADIUS)) continue

			walls += wall
		}
	}

	fun setFlag() {
		flag.pos.x = p.width / 2f
		flag.pos.y = 10f
	}

	fun reset() {
		population.reset()
		minSteps = Brain.STARTING_STEPS
	}
}
