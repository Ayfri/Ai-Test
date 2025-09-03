package entities

import p
import processing.core.PApplet
import processing.core.PVector
import java.awt.Color
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class Zone(
	val pos: PVector,
	val height: Float,
	val width: Float,
	val angle: Float = 0f
) {
	val halfWidth = width / 2f
	val halfHeight = height / 2f

	val x1: Float
	val y1: Float
	val x2: Float
	val y2: Float

	init {
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

	@Suppress("NOTHING_TO_INLINE")
	inline fun Float.squared() = this * this

	@Suppress("NOTHING_TO_INLINE")
	inline fun collidesWith(player: Player) = collidesWith(player.pos.x, player.pos.y, Player.RADIUS)

	/* fun collidesWith(x: Float, y: Float, radius: Float): Boolean {
		val xCircleDistance = abs(x - zone.pos.x)
		val yCircleDistance = abs(y - zone.pos.y)

		val halfWidth = zone.halfWidth
		val halfHeight = zone.halfHeight

		if (xCircleDistance > (halfWidth + radius)) return false
		if (yCircleDistance > (halfHeight + radius)) return false

		if (xCircleDistance <= halfWidth) return true
		if (yCircleDistance <= halfHeight) return true

		val cornerDistance = (xCircleDistance - halfWidth).squared() + (yCircleDistance - halfHeight).squared()

		return cornerDistance <= radius.squared()
	} */
	fun collidesWith(x: Float, y: Float, radius: Float): Boolean {
		val halfWidth = zone.halfWidth
		val halfHeight = zone.halfHeight
		val radiusPlusHalfWidth = radius + halfWidth

		val xCircleDistance = abs(x - zone.pos.x)
		val yCircleDistance = abs(y - zone.pos.y)

		if (xCircleDistance > radiusPlusHalfWidth) return false
		if (yCircleDistance > (halfHeight + radius)) return false

		if (xCircleDistance <= halfWidth) return true
		if (yCircleDistance <= halfHeight) return true

		val cornerDistance = (xCircleDistance - halfWidth).squared() + (yCircleDistance - halfHeight).squared()

		return cornerDistance <= radius.squared()
	}


	companion object {
		val heightRange = 10f..250f
		val widthRange = 10f..250f
	}
}
