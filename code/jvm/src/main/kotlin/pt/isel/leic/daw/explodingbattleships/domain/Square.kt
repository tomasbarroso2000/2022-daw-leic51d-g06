package pt.isel.leic.daw.explodingbattleships.domain

import java.lang.IllegalArgumentException

/**
 * Represents a square
 * @property row the square's row
 * @property column the square's column
 */
data class Square(
    val row: Char,
    val column: Int
) {
    /**
     * Get the square placed above the current square
     */
    fun up() =
        Square(row - 1, column)

    /**
     * Get the square placed below the current square
     */
    fun down() =
        Square(row + 1, column)

    /**
     * Get the square placed to the left of the current square
     */
    fun left() =
        Square(row, column - 1)

    /**
     * Get the square placed to the right of the current square
     */
    fun right() =
        Square(row, column + 1)

    /**
     * Get a string with the row and column of the square
     */
    override fun toString() = "$row$column"
}

/**
 * Converts the string to square or null
 */
fun String.toSquareOrNull() =
    try { Square(first(), subSequence(1, lastIndex + 1).toString().toInt()) } catch (e: Exception) { null }

/**
 * Converts the string to a square or throws an exception
 */
fun String.toSquare() = toSquareOrNull() ?: throw IllegalArgumentException("Invalid square")