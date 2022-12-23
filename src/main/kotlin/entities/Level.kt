package entities

import p
import processing.core.PApplet
import processing.core.PVector

class Level {
	val walls = mutableListOf<Wall>()
	val flag = Flag(PVector())
	val players = mutableListOf<Player>()
	val population = Population(this, players)
	var onlyDisplayBest = false

	fun draw() {
		flag.draw()
		if (!onlyDisplayBest) players.drop(1).forEach(Player::draw)
		players.first().draw()
	}

	fun update() {
		players.forEach { it.update(this) }
	}

	fun generateRandomWalls(count: Int) {
		walls.clear()
		for (i in 0 until count) {
			val x = p.random(p.width.toFloat())
			val y = p.random(p.height.toFloat())
			val width = p.random(Wall.widthRange.start, Wall.widthRange.endInclusive)
			val angle = p.random(PApplet.TWO_PI)
			walls += Wall(PVector(x, y), width, angle)
		}
	}

	fun setFlag() {
		flag.pos.x = p.width / 2f
		flag.pos.y = 10f
	}

	fun setPlayers(count: Int) {
		players.clear()
		for (i in 0 until count) {
			players += Player()
		}
	}

	fun reset() {
		setPlayers(players.size)
		population.reset()
	}
}
