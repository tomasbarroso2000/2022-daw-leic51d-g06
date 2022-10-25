package pt.isel.leic.daw.explodingbattleships.domain

import java.lang.IllegalArgumentException

typealias NextSquare = Square.() -> Square

data class Square(
    val row: Char,
    val column: Int
)

fun Square.down() =
    Square(row + 1, column)
fun Square.right() =
    Square(row, column + 1)

fun Square.getString() = "$row$column"

fun String.toSquareOrNull() =
    try { Square(first(), subSequence(1, lastIndex + 1).toString().toInt()) }
    catch (e: Exception) { null }

fun String.toSquareOrThrow() = this.toSquareOrNull() ?: throw IllegalArgumentException("Invalid Square")
