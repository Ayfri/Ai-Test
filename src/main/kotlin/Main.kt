import processing.core.PApplet

class Main : PApplet() {
	override fun settings() {
		size(500, 500)
	}

	override fun setup() {
		background(0)
	}

	override fun draw() {
		fill(255)
		ellipse(mouseX.toFloat(), mouseY.toFloat(), 50f, 50f)
	}
}

fun main() {
	PApplet.main(Main::class.java)
}
