package entities

import processing.core.PVector


data class Moves(
	val steps: Int,
	private val values: ByteArray = ByteArray(steps * 2) { 0 },
) {
	val size = steps

	private fun toIndex(index: Int) = index * 2
	private fun rawAt(i: Int) = values[i].toInt()
	private fun asValue(value: Int) = LUT[value]

	operator fun get(index: Int) = PVector(asValue(rawAt(toIndex(index))), asValue(rawAt(toIndex(index) + 1)))

	fun getInto(index: Int, out: PVector): PVector {
		val base = toIndex(index)
		out.x = asValue(rawAt(base))
		out.y = asValue(rawAt(base + 1))
		return out
	}

	fun xAt(index: Int) = asValue(rawAt(toIndex(index)))
	fun yAt(index: Int) = asValue(rawAt(toIndex(index) + 1))

	fun clone() = Moves(steps).also { values.copyInto(it.values) }

	operator fun set(index: Int, value: PVector) {
		// Store scaled values clamped to [0, MAX_VALUE]
		val x = ((value.x + 1) * PRECISION).toInt().coerceIn(0, MAX_VALUE)
		val y = ((value.y + 1) * PRECISION).toInt().coerceIn(0, MAX_VALUE)
		values[toIndex(index)] = x.toByte()
		values[toIndex(index) + 1] = y.toByte()
	}

	fun setRaw(index: Int, rawX: Int, rawY: Int) {
		val base = toIndex(index)
		values[base] = rawX.coerceIn(0, MAX_VALUE).toByte()
		values[base + 1] = rawY.coerceIn(0, MAX_VALUE).toByte()
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Moves) return false

		if (!values.contentEquals(other.values)) return false

		return true
	}

	override fun hashCode() = values.contentHashCode()

	fun stringRepresentation() = values.joinToString(",") { it.toString() }
	fun toList(): List<Int> = values.map { it.toInt() }
	fun toVectorList(): List<PVector> {
		val result = ArrayList<PVector>(steps)
		var i = 0
		while (i < values.size) {
			val x = asValue(values[i].toInt())
			val y = asValue(values[i + 1].toInt())
			result.add(PVector(x, y))
			i += 2
		}
		return result
	}

	companion object {
		const val PRECISION = 20
		const val MAX_VALUE = 2 * PRECISION
		private val LUT: FloatArray = FloatArray(MAX_VALUE + 1) { (it.toFloat() / PRECISION) - 1f }
	}
}

fun moves(size: Int, block: (Int) -> Int) = Moves(size, ByteArray(size * 2) { block(it).coerceIn(0, Moves.MAX_VALUE).toByte() })
