package entities

import p
import processing.core.PVector

data class Flag(var pos: PVector) {
	val diameter = 10f
	inline val halfDiameter get() = diameter / 2f

	fun draw() {
		p.pushMatrix()
		p.translate(pos.x, pos.y)
		p.fill(255f, 0f, 0f)
		p.ellipse(0f, 0f, diameter, diameter)
		p.popMatrix()
	}

	operator fun contains(point: PVector) =
		point.x > pos.x - halfDiameter && point.x < pos.x + halfDiameter && point.y > pos.y - halfDiameter && point.y < pos.y + halfDiameter
}
