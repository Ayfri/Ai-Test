import processing.core.PApplet
import kotlin.math.pow
import kotlin.math.roundToInt

val Number.radians: Float
	get() = PApplet.radians(this.toFloat())

val Number.degrees: Float
	get() = PApplet.degrees(this.toFloat())

fun Number.roundToDecimalPlaces(decimalPlaces: Int): Float {
	val factor = 10f.pow(decimalPlaces)
	return (this.toFloat() * factor).roundToInt() / factor
}
