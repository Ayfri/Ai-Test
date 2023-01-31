package entities

import p
import processing.core.PVector
import kotlin.math.abs
import kotlin.math.pow

data class Flag(var pos: PVector) {
	val diameter = 10f
	val radius = diameter / 2f

	fun draw() {
		p.push()
		p.fill(255f, 0f, 0f)
		p.ellipse(pos.x, pos.y, diameter, diameter)
		p.pop()
	}

	fun collidesWith(player: Player): Boolean {
		val xDistance = abs(player.pos.x - pos.x)
		val yDistance = abs(player.pos.y - pos.y)
		val distance = xDistance.pow(2) + yDistance.pow(2)
		return distance < (Player.RADIUS + radius).pow(2)
	}
}
