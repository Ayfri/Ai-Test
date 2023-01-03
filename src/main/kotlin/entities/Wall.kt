package entities

import p
import processing.core.PVector
import java.awt.Color
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

	val left = minOf(x1, x2)
	val right = maxOf(x1, x2)
	val top = minOf(y1, y2)
	val bottom = maxOf(y1, y2)
}

data class Wall(val pos: PVector, val width: Float, val angle: Float) {
	private val color = Color.HSBtoRGB(p.random(0f, 1f), p.random(0f, 1f), 1f)
	val zone = Zone(
		pos = pos,
		height = HEIGHT,
		width = width,
		angle = angle
	)

	fun draw() {
		p.fill(color)
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
