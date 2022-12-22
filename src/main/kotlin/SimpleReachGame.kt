import entities.Brain
import entities.Level
import entities.Player
import entities.Wall
import processing.core.PApplet
import processing.core.PVector

val collisions = mutableSetOf<PVector>()

class SimpleReachGame : PApplet() {
	private var movingFlag = false
	var level = Level()
	var speed = 10f
		set(value) {
			if (value <= 0) return

			field = value
			frameRate(60 * speed)
		}

	var hoverPlayer: Player? = null

	override fun settings() {
		size(1600, 900)
	}

	override fun setup() {
		background(255f)
		frameRate(60 * speed)
		p = this

		level.generateRandomWalls(60)
		level.setFlag()
		level.setPlayers(800)
	}

	fun text(text: String, column: Int) = text(text, 15f, 30f * column + 1)

	override fun draw() {
		if (movingFlag) return
		background(255f)
		level.walls.forEach(Wall::draw)

		if (level.population.isFinished) {
			generation()
		} else {
			level.draw()
			level.update()
		}
		/*collisions.forEach { collision ->
			fill(255f, 0f, 0f)
			strokeWeight(0f)
			circle(collision.x, collision.y, 2f)
			strokeWeight(1f)
		}*/

		fill(0f)
		textSize(22f)

		text("Step: ${level.players[0].brain.step}", 1)
		text("Speed: $speed", 2)
		text("Framerate: $frameRate", 3)
		text("Min steps: ${level.population.minSteps}", 5)
		text("Generation: ${level.population.generation}", 6)
		text("Population: ${level.players.size}", 7)
//		text("Fitness sum: ${level.population.fitnessSum}", 8)
		text("Mutation rate: ${Brain.mutationRate}", 8)

		hoverPlayer?.let {
			text("Fitness: ${it.fitness}", 10)
			text("Velocity: ${it.velocity}", 11)
			text("Position: ${it.pos}", 12)
		}
	}

	private fun generation() {
		level.population.calculateFitness()
		level.population.naturalSelection()
		level.population.mutatePlayers()
		collisions.clear()
	}

	override fun mouseMoved() {
		hoverPlayer = level.players.firstOrNull { it.collidesWith(PVector(mouseX.toFloat(), mouseY.toFloat())) }
	}

	override fun keyPressed() {
		when (key) {
			'+' -> speed += .1f
			'-' -> speed -= .1f
			'f' -> {
				movingFlag = true
				level.flag.pos = PVector(mouseX.toFloat(), mouseY.toFloat())
				level.reset()
				movingFlag = false
			}

			'r' -> level = Level().apply {
				generateRandomWalls(60)
				setFlag()
				setPlayers(1200)
			}
		}

		when (keyCode) {
			UP -> Brain.mutationRate += .005f
			DOWN -> Brain.mutationRate -= .005f
		}
	}
}

lateinit var p: PApplet
fun main() {
	PApplet.main(SimpleReachGame::class.java)
}
