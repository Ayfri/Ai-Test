
import entities.Brain
import entities.Level
import entities.Player
import entities.Wall
import processing.core.PApplet
import processing.core.PVector
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.max

val collisions = mutableSetOf<PVector>()
val line = mutableSetOf<PVector>()

class SimpleReachGame : PApplet() {
	private var movingFlag = false
	var level = Level()

	@Volatile
	var speed = 10f
		set(value) {
			if (value <= 0) return
			field = value
			deleteUpdate()
			setupUpdate()
		}

	var hoverPlayer: Player? = null

	lateinit var executor: ScheduledExecutorService

	override fun settings() {
		size(1600, 900)
	}

	override fun setup() {
		background(255f)
		frameRate(60f)
		p = this

		level.generateRandomWalls(60)
		level.setFlag()
		level.setPlayers()
		setupUpdate()
	}

	private fun setupUpdate(): ScheduledFuture<*>? {
		executor = Executors.newSingleThreadScheduledExecutor()

		return executor.scheduleAtFixedRate({
			level.update()
		}, 0, max((1000 / 30 / speed).toLong(), 1), TimeUnit.MILLISECONDS)
	}

	private fun deleteUpdate() {
		executor.shutdown()
		executor.awaitTermination(1, TimeUnit.SECONDS)
		while (!executor.isTerminated) delay(1)
	}

	fun text(text: String, column: Int) = text(text, 15f, 30f * column + 1)

	override fun draw() {
		if (movingFlag) return
		background(255f)
		level.walls.forEach(Wall::draw)

		if (level.population.isFinished) {
			deleteUpdate()
			changeGeneration()
			setupUpdate()
		} else {
			level.draw()
		}
		/*collisions.forEach { collision ->
			stroke(255f, 0f, 0f)
			point(collision.x, collision.y)
			strokeWeight(1f)
		}*/
		beginShape()
		noFill()
		stroke(0f, 0f, 255f)
		strokeWeight(2f)
		line.forEach { point ->
			curveVertex(point.x, point.y)
		}
		endShape()
		strokeWeight(.75f)
		stroke(0f)

		fill(0f)
		textSize(22f)

		text("Step: ${level.players[0].brain.step}", 1)
		text("Speed: $speed", 2)
		text("Framerate: $frameRate", 3)
		text("Min steps: ${level.population.minSteps}", 5)
		text("Generation: ${level.population.generation}", 6)
		text("Population: ${level.players.size}", 7)
		text("Fitness sum: ${level.population.fitnessSum}", 8)
		text("Mutation rate: ${(Brain.mutationRate * 100).roundToDecimalPlaces(2)}%", 9)

		hoverPlayer?.let {
			text("Velocity: ${it.velocity}", 12)
			text("Position: ${it.pos}", 13)
		}
	}

	private fun changeGeneration() {
		level.population.calculateFitness()
		level.population.naturalSelection()
		level.population.mutatePlayers()
		collisions.clear()
		line.clear()
	}

	override fun mouseMoved() {
		hoverPlayer = level.players.firstOrNull { it.collidesWith(PVector(mouseX.toFloat(), mouseY.toFloat())) }
	}

	override fun keyPressed() {
		when (key) {
			'+' -> speed += .25f
			'-' -> speed -= .25f
			'f' -> {
				movingFlag = true
				deleteUpdate()
				level.flag.pos = PVector(mouseX.toFloat(), mouseY.toFloat())
				level.reset()
				line.clear()
				setupUpdate()
				movingFlag = false
			}

			'r' -> level = Level().apply {
				generateRandomWalls(60)
				setFlag()
				setPlayers()
			}

			'd' -> {
				level.onlyDisplayBest = !level.onlyDisplayBest
			}
		}

		when (keyCode) {
			UP -> Brain.mutationRate += .0025f
			DOWN -> Brain.mutationRate -= .0025f
		}
	}
}

lateinit var p: PApplet
fun main() {
	PApplet.main(SimpleReachGame::class.java)
}
