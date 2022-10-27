package pt.isel.leic.daw.explodingbattleships.domain

import java.lang.IllegalArgumentException

typealias NextSquare = Square.() -> Square

/**
 * Represents a square
 * @property row the square's row
 * @property column the square's column
 */
data class Square(
    val row: Char,
    val column: Int
)

/**
 * Get the square placed in the row bellow the current square
 */
fun Square.down() =
    Square(row + 1, column)

/**
 * Get the square placed in the row above the current square
 */
fun Square.right() =
    Square(row, column + 1)

/**
 * Get a string with the row and column of the square
 */
fun Square.getString() = "$row$column"

/**
 * Converts the string to square or null
 */
fun String.toSquareOrNull() =
    try { Square(first(), subSequence(1, lastIndex + 1).toString().toInt()) } catch (e: Exception) { null }

/**
 * Converts the string to a square or throws an exception
 */
fun String.toSquareOrThrow() = this.toSquareOrNull() ?: throw IllegalArgumentException("Invalid Square")
