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

val bestPlayerPath = mutableListOf<PVector>()
var pathDisplayStep = 0
val line = mutableSetOf<PVector>()
val averageCollisionCalculationTime = ArrayList<Long>(1000)

class SimpleReachGame : PApplet() {
	private var movingFlag = false
	lateinit var level: Level
	var timing = 10.minutes
	var startTime = 0L

	@Volatile
	var speed = 0.125f
		set(value) {
			if (value !in 0.0F..MAX_SPEED) return
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
		frameRate(170f)
		p = this
		level = Level()
		rectMode(CORNERS)
		ellipseMode(RADIUS)

		level.generateRandomWalls(60)
		level.setFlag()
		level.population.setPlayers()
		setupUpdate()
		startTime = System.currentTimeMillis()
	}

	private fun setupUpdate() = if (speed < MAX_SPEED) Executors.newSingleThreadScheduledExecutor().let {
		it.scheduleAtFixedRate({
			level.update()
		}, 0, max((5_000_000 / speed).toLong(), 1), TimeUnit.NANOSECONDS)
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

		displayLine()

		textSize(22f)
		fill(0f)

		text("Step: ${level.step}", 1)
		text("Speed: $speed", 2)
		text("Framerate: $frameRate", 3)
		val collisionCalculationTime = averageCollisionCalculationTime.toList()
		text("Average collision calculation time: ${collisionCalculationTime.average()}ns", 4)
		text("Time: ${timing.inWholeMilliseconds}ms", 5)
		text("Min steps: ${level.minSteps}", 6)
		text("Generation: ${level.population.generation}", 7)
		text("Population: ${level.population.players.size}", 8)
		text("Fitness sum: ${level.population.fitnessSum}", 9)
		text("Mutation rate: ${(Brain.mutationRate * 100).roundToDecimalPlaces(2)}%", 10)

		hoverPlayer?.let {
			text("Velocity: ${it.velocity}", 12)
			text("Position: ${it.pos}", 13)
		}
	}

	@Suppress("NOTHING_TO_INLINE")
	private inline fun displayLine() {
		if (bestPlayerPath.isEmpty()) return

		push()
		beginShape()
		noFill()
		stroke(0f, 0f, 255f)
		strokeWeight(2f)

		// Always sync with actual player step + 1, regardless of speed
		val targetDisplayStep = minOf(level.step + 1, bestPlayerPath.size - 1)

		// Filter out consecutive duplicate points to avoid curve issues
		var lastPoint: PVector? = null
		for (i in 0..targetDisplayStep) {
			val point = bestPlayerPath[i]
			// Only add curveVertex if point is different from the previous one
			if (lastPoint == null || point.x != lastPoint.x || point.y != lastPoint.y) {
				curveVertex(point.x, point.y)
				lastPoint = point
			}
		}

		endShape()
		pop()
	}

	private fun changeGeneration() {
		timing = (System.currentTimeMillis() - startTime).milliseconds
		startTime = System.currentTimeMillis()
		level.population.calculateFitness()

		// Pre-calculate the best player's path before natural selection
		preCalculateBestPlayerPath()

		level.population.naturalSelection()
		level.population.mutatePlayers()
		level.step = 0
		line.clear()
		pathDisplayStep = 0
	}

	private fun preCalculateBestPlayerPath() {
		bestPlayerPath.clear()
		val bestPlayer = level.population.getBestPlayer()

		// Create a simulation copy of the best player
		val simulationPlayer = Player(
			pos = Player.startingPoint.copy(),
			brain = bestPlayer.brain
		)

		// Add starting position
		bestPlayerPath.add(simulationPlayer.pos.copy())

		// Simulate the player's movement step by step
		var step = 0
		while (step < level.minSteps && !simulationPlayer.hasReachedGoal) {
			simulationPlayer.move(level, step)
			simulationPlayer.checkCollision(level)
			bestPlayerPath.add(simulationPlayer.pos.copy())
			step++
		}
	}

	override fun mouseMoved() {
//		hoverPlayer = level.population.players.firstOrNull { it.collidesWith(PVector(mouseX.toFloat(), mouseY.toFloat())) }
	}

	override fun keyPressed() {
		when (key) {
			'+' -> speed += .125f
			'-' -> speed -= .125f
			'f' -> {
				movingFlag = true
				deleteUpdate()
				level.flag.pos = PVector(mouseX.toFloat(), mouseY.toFloat())
				level.reset()
				line.clear()
				bestPlayerPath.clear()
				pathDisplayStep = 0
				setupUpdate()
				movingFlag = false
			}

			'r' -> level = Level().apply {
				generateRandomWalls(60)
				setFlag()
				population.setPlayers()
				line.clear()
				bestPlayerPath.clear()
				pathDisplayStep = 0
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
		const val MAX_SPEED = 10.0F
	}
}

lateinit var p: PApplet
fun main() {
	PApplet.main(SimpleReachGame::class.java)
}
