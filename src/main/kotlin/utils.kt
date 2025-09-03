
import processing.core.PApplet
import processing.core.PVector
import java.util.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

operator fun PVector.component1() = x
operator fun PVector.component2() = y

val Number.radians: Float
	get() = PApplet.radians(this.toFloat())

val Number.degrees: Float
	get() = PApplet.degrees(this.toFloat())

fun Number.roundToDecimalPlaces(decimalPlaces: Int): Float {
	val factor = 10f.pow(decimalPlaces)
	return (this.toFloat() * factor).roundToInt() / factor
}

val fastRandom = SplittableRandom()

@Suppress("NOTHING_TO_INLINE")
inline fun fastRandomVec(): PVector {
	val angle = fastRandom.nextDouble(0.0, 2 * Math.PI)
	return PVector(cos(angle).toFloat(), sin(angle).toFloat())
}
