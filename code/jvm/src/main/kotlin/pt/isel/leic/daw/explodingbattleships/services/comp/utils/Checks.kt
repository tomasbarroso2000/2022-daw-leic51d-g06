package pt.isel.leic.daw.explodingbattleships.services.comp.utils

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.NextSquare
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.ShipType
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.domain.down
import pt.isel.leic.daw.explodingbattleships.domain.getString
import pt.isel.leic.daw.explodingbattleships.domain.right

fun checkOrThrow(undesiredCondition: Boolean, errorMessage: String) {
    if (undesiredCondition)
        throw AppException(errorMessage, AppExceptionStatus.BAD_REQUEST)
}

/**
 * Checks if the limit and skip values are valid and
 * throws an exception they are not
 * @param limit the given limit
 * @param skip the given skip
 */
fun checkLimitAndSkip(limit: Int, skip: Int) {
    checkOrThrow(limit <= 0, "Invalid limit")
    checkOrThrow(skip < 0, "Invalid skip")
}

fun checkShipLayout(ships: List<Ship>, width: Int, height: Int) {
    checkOrThrow(ships.size != 5, "Can only place 5 ships")
    checkOrThrow(!shipsValid(ships), "Invalid ship list")
    val occupiedSquares = mutableSetOf<Square?>()
    ships.forEach { ship ->
        checkOrThrow(ship.square == null, "No square provided for ${ship.name}")
        checkOrThrow(ship.orientation == null, "No orientation provided for ${ship.name}")
        when (ship.orientation?.lowercase()) {
            "vertical" -> validateShipSquares(ship, width, height, occupiedSquares, Square::down)
            "horizontal" -> validateShipSquares(ship, width, height, occupiedSquares, Square::right)
            else -> throw AppException("Invalid orientation for ${ship.name}", AppExceptionStatus.BAD_REQUEST)
        }
    }
}

private fun shipsValid(ships: List<Ship>) =
    ships.map { it.name }.containsAll(ShipType.values().map { it.shipName })

fun squareInBoard(square: Square?, width: Int, height: Int): Boolean {
    if (square == null) return false
    val lastRow = square.row?.plus(height)?.minus(1) ?: return false
    val lastColumn = square.column?.plus(width)?.minus(1) ?: return false
    if (square.row !in 'a'..lastRow)
        return false
    if (square.column !in 1..lastColumn)
        return false
    return true
}

private fun validateShipSquares(ship: Ship, width: Int, height: Int, occupiedSquares: MutableSet<Square?>, nextSquare: NextSquare) {
    val shipSize = ShipType.values().find { it.shipName == ship.name }?.size
        ?: throw IllegalStateException("No ship found with the name ${ship.name}")
    var currentSquare = ship.square
    for (i in 0 until shipSize) {
        checkOrThrow(!squareInBoard(currentSquare, width, height), "Invalid square on ${currentSquare.getString()}")
        checkOrThrow(occupiedSquares.contains(currentSquare), "Square already occupied on ${currentSquare.getString()}")
        occupiedSquares.add(currentSquare)
        currentSquare = currentSquare?.nextSquare()
    }
}

fun computePlayerId(transaction: Transaction, token: String?, data: Data): Int {
    if (token.isNullOrBlank())
        throw AppException("No token provided", AppExceptionStatus.UNAUTHORIZED)
    return data.playersData.getPlayerIdFromToken(transaction, token)
        ?: throw AppException("Invalid token", AppExceptionStatus.UNAUTHORIZED)
}

fun computeGame(transaction: Transaction, gameId: Int?, data: Data): Game {
    if (gameId == null || gameId <= 0)
        throw AppException("Invalid gameId", AppExceptionStatus.BAD_REQUEST)
    return data.gamesData.getGame(transaction, gameId)
        ?: throw AppException("Game does not exist", AppExceptionStatus.NOT_FOUND)
}

fun checkGameState(gameState: String, state: String) =
    checkOrThrow(gameState != state, "Invalid game state")

fun checkPlayerInGame(game: Game, playerId: Int) =
    checkOrThrow(game.player1 != playerId && game.player2 != playerId, "Wrong game")

fun checkCurrentPlayer(game: Game, playerId: Int) =
    checkOrThrow(game.currPlayer != playerId, "Not your turn")
