package pt.isel.leic.daw.explodingbattleships.services.comp.utils

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.ShipType
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.domain.down
import pt.isel.leic.daw.explodingbattleships.domain.getString
import pt.isel.leic.daw.explodingbattleships.domain.right
import java.util.regex.Pattern

/**
 * Executes a function within a [Transaction]
 * @param transaction the [Transaction] to be used
 * @param function the code to be executed
 * @return the value returned by function
 */
fun <T> executeTransaction(transaction: Transaction, function: (t: Transaction) -> T): T {
    try {
        transaction.begin()
        val res = function(transaction)
        transaction.commit()
        return res
    } catch (error: Exception) {
        transaction.rollback()
        throw error
    } finally {
        transaction.close()
    }
}

/**
 * Executes a service with the necessary catch block
 * @param data the data module to be used
 * @param function the function to be executed
 * @return the value returned by function
 */
fun <T> doService(data: Data, function: (t: Transaction) -> T): T {
    val transaction = data.getTransaction()
    try {
        return executeTransaction(transaction) {
            function(it)
        }
    } catch (error: Exception) {
        if (error is AppException) throw error
        else throw handleDataError(error)
    }
}

/**
 * Sends the token to getPlayerIdByToken in the data module
 * @param token the token linked to the player
 * @param data the data module
 * @return [Int] the id of the player
 */
fun getPlayerId(transaction: Transaction, token: String?, data: Data): Int {
    if (token == null || token == "") throw AppException("No token provided", AppExceptionStatus.UNAUTHORIZED)
    return data.playerData.getPlayerIdByToken(transaction, token)
        ?: throw AppException("Invalid token", AppExceptionStatus.UNAUTHORIZED)
}

/**
 * Check if the email address is valid
 */
fun isEmailValid(email: String): Boolean {
    return Pattern.compile("^(.+)@(\\S+)$")
        .matcher(email)
        .matches()
}

/**
 * Checks if the limit and skip values are valid and
 * throws an exception they are not
 * @param limit the given limit
 * @param skip the given skip
 */
fun checkLimitAndSkip(limit: Int, skip: Int) {
    if (limit <= 0)
        throw AppException("Invalid limit", AppExceptionStatus.BAD_REQUEST)
    if (skip < 0)
        throw AppException("Invalid skip", AppExceptionStatus.BAD_REQUEST)
}

fun checkShipLayout(ships: List<Ship>, width: Int, height: Int) {
    if (ships.size != 5)
        throw AppException("Can only place 5 ships", AppExceptionStatus.BAD_REQUEST)
    if (!checkShipsValid(ships))
        throw AppException("Invalid ship list", AppExceptionStatus.BAD_REQUEST)
    val occupiedSquares = mutableSetOf<Square?>()
    ships.forEach { ship ->
        if (ship.square == null)
            throw AppException("No square provided for ${ship.name}", AppExceptionStatus.BAD_REQUEST)
        if (ship.orientation == null)
            throw AppException("No orientation provided for ${ship.name}", AppExceptionStatus.BAD_REQUEST)
        when (ship.orientation.lowercase()) {
            "vertical" -> checkShipSquares(ship, width, height, occupiedSquares, Square::down)
            "horizontal" -> checkShipSquares(ship, width, height, occupiedSquares, Square::right)
            else -> throw AppException("Invalid orientation for ${ship.name}", AppExceptionStatus.BAD_REQUEST)
        }
    }
}

private fun checkShipsValid(ships: List<Ship>) =
    ships.map { it.name }.containsAll(ShipType.values().map { it.shipName })

private fun squareInBoard(square: Square?, width: Int, height: Int): Boolean {
    if (square == null) return false
    val lastRow = square.row?.plus(height)?.minus(1) ?: return false
    val lastColumn = square.column?.plus(width)?.minus(1) ?: return false
    if (square.row !in 'a'..lastRow)
        return false
    if (square.column !in 1..lastColumn)
        return false
    return true
}

private fun checkShipSquares(
    ship: Ship,
    width: Int,
    height: Int,
    occupiedSquares: MutableSet<Square?>,
    nextShip: Square.() -> Square
) {
    val shipSize = ShipType.values().find { it.shipName == ship.name }?.size ?: throw IllegalStateException()
    var currentSquare = ship.square
    for (i in 0 until shipSize) {
        if (occupiedSquares.contains(currentSquare))
            throw AppException("Square already occupied on ${currentSquare.getString()}")
        if (!squareInBoard(currentSquare, width, height))
            throw AppException("Invalid square on ${currentSquare.getString()}")
        occupiedSquares.add(currentSquare)
        currentSquare = currentSquare?.nextShip()
    }
}
