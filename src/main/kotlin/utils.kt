import processing.core.PApplet
import processing.core.PVector
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

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
inline fun fastRandomVec() = PVector(fastRandom.nextFloat(-1f, 1f), fastRandom.nextFloat(-1f, 1f))
