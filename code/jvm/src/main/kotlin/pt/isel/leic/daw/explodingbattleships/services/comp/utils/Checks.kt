package pt.isel.leic.daw.explodingbattleships.services.comp.utils

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.*

/**
 * Throws an [AppException] if the undesired condition is verified
 * @param undesiredCondition the undesired condition
 * @param errorMessage the error message to be thrown
 */
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

fun checkShipLayout(ships: List<UnverifiedShip>, width: Int, height: Int): List<VerifiedShip> {
    checkOrThrow(ships.size != 5, "Can only place 5 ships")
    checkOrThrow(!shipsValid(ships), "Invalid ship list")
    val occupiedSquares = mutableSetOf<VerifiedSquare?>()
    val verifiedShips = mutableListOf<VerifiedShip>()
    ships.forEach { unverifiedShip ->
        val verifiedShip = unverifiedShip.toVerifiedShipOrNull()
            ?: throw AppException("Invalid ship", AppExceptionStatus.BAD_REQUEST)
        when (verifiedShip.orientation.lowercase()) {
            "vertical" -> validateShipSquares(verifiedShip, width, height, occupiedSquares, VerifiedSquare::down)
            "horizontal" -> validateShipSquares(verifiedShip, width, height, occupiedSquares, VerifiedSquare::right)
            else -> throw AppException("Invalid orientation for ${verifiedShip.name}", AppExceptionStatus.BAD_REQUEST)
        }
    }
    return verifiedShips
}

/**
 * Checks if a list of ships is a valid one
 * @param ships the list of ships
 * @return true if the list is valid
 */
private fun shipsValid(ships: List<UnverifiedShip>) =
    ships.map { it.name }.containsAll(ShipType.values().map { it.shipName })

/**
 * Checks if a square is within a board
 * @param square the square in question
 * @param width the width of the board
 * @param height the height of the board
 */
fun squareInBoard(square: VerifiedSquare, width: Int, height: Int): Boolean {
    val lastRow = square.row + height - 1
    val lastColumn = square.column + width - 1
    if (square.row !in 'a'..lastRow) return false
    if (square.column !in 1..lastColumn) return false
    return true
}

/**
 * Validates the squares of a [UnverifiedShip]
 * @param ship the [UnverifiedShip]
 * @param width the width of the board
 * @param height the height of the board
 * @param occupiedSquares the occupied squares
 * @param nextSquare the function to calculate the next square
 */
private fun validateShipSquares(ship: VerifiedShip, width: Int, height: Int, occupiedSquares: MutableSet<VerifiedSquare?>, nextSquare: NextSquare) {
    val shipSize = ship.getSize()
    var currentSquare = ship.firstSquare
    for (i in 0 until shipSize) {
        checkOrThrow(!squareInBoard(currentSquare, width, height), "Invalid square on ${currentSquare.getString()}")
        checkOrThrow(occupiedSquares.contains(currentSquare), "Square already occupied on ${currentSquare.getString()}")
        occupiedSquares.add(currentSquare)
        currentSquare = currentSquare.nextSquare()
    }
}

/**
 * Computes a player through a token
 * @param transaction the current transaction
 * @param token the user token
 * @param data the [Data] module
 * @return the player
 */
fun computePlayer(transaction: Transaction, token: String?, data: Data): Player {
    if (token.isNullOrBlank())
        throw AppException("No token provided", AppExceptionStatus.UNAUTHORIZED)
    return data.playersData.getPlayerFromToken(transaction, token)
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
