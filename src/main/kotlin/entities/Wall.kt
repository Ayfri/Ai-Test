package entities

import p
import processing.core.PApplet
import processing.core.PVector
import java.awt.Color
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
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

data class Wall(val pos: PVector, val height: Float, val width: Float, val angle: Float) {
	private val color = Color.HSBtoRGB(p.random(0f, 1f), p.random(0f, 1f), 1f)
	val zone = Zone(
		pos = pos,
		height = height,
		width = width,
		angle = angle
	)

	fun draw() {
		p.push()
		p.fill(color)
		p.rectMode(PApplet.CENTER)
		p.translate(zone.pos.x, zone.pos.y)
		p.rect(0f, 0f, zone.width, zone.height)
		p.pop()
	}

	fun collidesWith(player: Player): Boolean {
		val xCircleDistance = abs(player.pos.x - zone.pos.x)
		val yCircleDistance = abs(player.pos.y - zone.pos.y)

		val halfWidth = zone.width / 2
		val halfHeight = zone.height / 2

		if (xCircleDistance > (halfWidth + Player.RADIUS)) return false
		if (yCircleDistance > (halfHeight + Player.RADIUS)) return false

		if (xCircleDistance <= halfWidth) return true
		if (yCircleDistance <= halfHeight) return true

		val cornerDistance = (xCircleDistance - halfWidth).pow(2) + (yCircleDistance - halfHeight).pow(2)

		return cornerDistance <= Player.RADIUS.pow(2)
	}

	companion object {
		val heightRange = 10f..250f
		val widthRange = 10f..250f
	}
}
