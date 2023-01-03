package entities

import p
import processing.core.PVector

data class Flag(var pos: PVector) {
	val diameter = 10f
	inline val halfDiameter get() = diameter / 2f

	fun draw() {
		p.fill(255f, 0f, 0f)
		p.ellipse(pos.x, pos.y, diameter, diameter)
	}

	operator fun contains(point: PVector) =
		point.x > pos.x - halfDiameter && point.x < pos.x + halfDiameter && point.y > pos.y - halfDiameter && point.y < pos.y + halfDiameter
}
