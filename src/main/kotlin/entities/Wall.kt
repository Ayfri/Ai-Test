package entities

import p
import processing.core.PApplet
import processing.core.PVector
import kotlin.math.cos
import kotlin.math.sin

data class Zone(val pos: PVector, val height: Float, val width: Float, val angle: Float = 0f) {
	val x1: Float
	val y1: Float
	val x2: Float
	val y2: Float

	init {
		val halfWidth = width / 2f
		val halfHeight = height / 2f
		val x = pos.x
		val y = pos.y
		val cos = cos(angle)
		val sin = sin(angle)

		x1 = x + (cos * -halfWidth) + (sin * -halfHeight)
		y1 = y + (cos * -halfHeight) + (sin * halfWidth)
		x2 = x + (cos * halfWidth) + (sin * halfHeight)
		y2 = y + (cos * halfHeight) + (sin * -halfWidth)
	}

	val left get() = minOf(x1, x2)
	val right get() = maxOf(x1, x2)
	val top get() = minOf(y1, y2)
	val bottom get() = maxOf(y1, y2)
	val center get() = PVector(x1 + width / 2f, y1 + height / 2f)
}

data class Wall(val pos: PVector, val width: Float, val angle: Float) {
	val zone = Zone(
		pos = pos,
		height = HEIGHT,
		width = width,
		angle = angle
	)

	fun draw() {
		p.fill(0f, 255f, 0f)
		p.rectMode(PApplet.CORNERS)
		p.rect(zone.x1, zone.y1, zone.x2, zone.y2)
	}

	fun collidesWith(player: Player): Boolean {
		val points = listOf(
			PVector(player.pos.x - Player.RADIUS, player.pos.y - Player.RADIUS),
			PVector(player.pos.x + Player.RADIUS, player.pos.y - Player.RADIUS),
			PVector(player.pos.x + Player.RADIUS, player.pos.y + Player.RADIUS),
			PVector(player.pos.x - Player.RADIUS, player.pos.y + Player.RADIUS)
		)

		return points.any {
			it.x > zone.left && it.x < zone.right && it.y > zone.top && it.y < zone.bottom
		}
	}

	companion object {
		const val HEIGHT = 12f
		val widthRange = 60f..300f
	}
}