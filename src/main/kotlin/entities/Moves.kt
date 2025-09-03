package entities

import processing.core.PVector


data class Moves(
	val steps: Int,
	private val values: IntArray = IntArray(steps * 2) { 0 },
) {
	val size = steps

	private inline fun toIndex(index: Int) = index * 2
	private inline fun asValue(value: Int) = (value.toFloat() / PRECISION) - 1

	operator fun get(index: Int) = PVector(asValue(values[toIndex(index)]), asValue(values[toIndex(index) + 1]))

	fun clone() = Moves(steps).also { values.copyInto(it.values) }

	operator fun set(index: Int, value: PVector) {
		// Pas besoin de normaliser ici, on prend les valeurs directement
		values[toIndex(index)] = ((value.x + 1) * PRECISION).toInt()
		values[toIndex(index) + 1] = ((value.y + 1) * PRECISION).toInt()
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Moves) return false

		if (!values.contentEquals(other.values)) return false

		return true
	}

	override fun hashCode(): Int {
		return values.contentHashCode()
	}

	fun stringRepresentation() = values.joinToString(",") { it.toString() }
	fun toList() = values.toList()
	fun toVectorList() = values.toList().chunked(2) { (x, y) -> PVector(asValue(x), asValue(y)) }

	companion object {
		const val PRECISION = 20
		const val MAX_VALUE = 2 * PRECISION
	}
}

fun moves(size: Int, block: (Int) -> Int) = Moves(size, IntArray(size * 2) { block(it) })
