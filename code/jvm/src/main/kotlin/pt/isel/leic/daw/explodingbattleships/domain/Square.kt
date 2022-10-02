package pt.isel.leic.daw.explodingbattleships.domain

typealias NextSquare = VerifiedSquare.() -> VerifiedSquare

interface Square {
    val row: Char?
    val column: Int?
}

data class UnverifiedSquare(
    override val row: Char?,
    override val column: Int?
) : Square

data class VerifiedSquare(
    override val row: Char,
    override val column: Int
) : Square

fun UnverifiedSquare.toVerifiedSquareOrNull(): VerifiedSquare? {
    row ?: return null
    column ?: return null
    return VerifiedSquare(row, column)
}

fun VerifiedSquare.down() = VerifiedSquare(row + 1 , column)
fun VerifiedSquare.right() = VerifiedSquare(row, column + 1)

fun Square.getString() = "$row$column"

fun String.toVerifiedSquare() = VerifiedSquare(first(), subSequence(1, lastIndex + 1).toString().toInt())