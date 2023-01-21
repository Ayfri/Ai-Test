package entities

import p
import processing.core.PApplet
import processing.core.PVector

class Level {
	val walls = mutableListOf<Wall>()
	val flag = Flag(PVector())
	val population = Population(this)
	var onlyDisplayBest = false

	fun draw() {
		flag.draw()
		population.draw()
	}

	fun update() {
		population.update()
	}

	fun generateRandomWalls(count: Int) {
		walls.clear()
		for (i in 0..count) {
			val x = p.random(p.width.toFloat())
			val y = p.random(p.height.toFloat())
			val height = p.random(Wall.heightRange.start, Wall.heightRange.endInclusive)
			val width = p.random(Wall.widthRange.start, Wall.widthRange.endInclusive)
			val angle = p.random(PApplet.TWO_PI)
			walls += Wall(PVector(x, y), height, width, angle)
		}
	}

	fun setFlag() {
		flag.pos.x = p.width / 2f
		flag.pos.y = 10f
	}

	fun reset() = population.reset()
}
