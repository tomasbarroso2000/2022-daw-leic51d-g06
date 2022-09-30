package pt.isel.leic.daw.explodingbattleships.domain

typealias NextSquare = Square.() -> Square

data class Square(
    val row: Char?,
    val column: Int?
)
fun Square.down() = Square(row?.plus(1), column)
fun Square.right() = Square(row, column?.plus(1))

fun Square?.getString() = "${this?.row}${this?.column}"

fun String.toSquare() = Square(first(), subSequence(1, lastIndex).toString().toInt())