import processing.core.PApplet

val Number.radians: Float
	get() = PApplet.radians(this.toFloat())

val Number.degrees: Float
	get() = PApplet.degrees(this.toFloat())
