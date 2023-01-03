
import entities.Brain
import entities.Level
import entities.Player
import entities.Wall
import processing.core.PApplet
import processing.core.PVector
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

val collisions = mutableSetOf<PVector>()
val line = mutableSetOf<PVector>()

class SimpleReachGame : PApplet() {
	private var movingFlag = false
	var level = Level()
	var timing = 10.minutes
	var startTime = 0L

	@Volatile
	var speed = 2f
		set(value) {
			if (value < 0 || value > MAX_SPEED) return
			field = value
			deleteUpdate()
			setupUpdate()
		}

	var hoverPlayer: Player? = null

	lateinit var executor: ExecutorService

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
		startTime = System.currentTimeMillis()
	}

	private fun setupUpdate() = if (speed < MAX_SPEED) Executors.newSingleThreadScheduledExecutor().let {
		it.scheduleAtFixedRate({
			level.update()
		}, 0, max((10_000_000 / speed).toLong(), 1), TimeUnit.NANOSECONDS)
		executor = it
	} else Executors.newSingleThreadScheduledExecutor().let {
		it.scheduleAtFixedRate({
			level.update()
		}, 0, 1, TimeUnit.NANOSECONDS)

		executor = it
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
			level.draw()
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
		line.forEach { (x, y) -> curveVertex(x, y) }
		endShape()
		strokeWeight(.75f)
		stroke(0f)

		fill(0f)
		textSize(22f)

		text("Step: ${level.players[0].brain.step}", 1)
		text("Speed: $speed", 2)
		text("Framerate: $frameRate", 3)
		text("Time: ${timing.inWholeMilliseconds}ms", 5)
		text("Min steps: ${level.population.minSteps}", 6)
		text("Generation: ${level.population.generation}", 7)
		text("Population: ${level.players.size}", 8)
		text("Fitness sum: ${level.population.fitnessSum}", 9)
		text("Mutation rate: ${(Brain.mutationRate * 100).roundToDecimalPlaces(2)}%", 10)

		hoverPlayer?.let {
			text("Velocity: ${it.velocity}", 12)
			text("Position: ${it.pos}", 13)
		}
	}

	private fun changeGeneration() {
		timing = (System.currentTimeMillis() - startTime).milliseconds
		startTime = System.currentTimeMillis()
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
				line.clear()
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

	companion object {
		const val MAX_SPEED = 20f
	}
}

lateinit var p: PApplet
fun main() {
	PApplet.main(SimpleReachGame::class.java)
}
