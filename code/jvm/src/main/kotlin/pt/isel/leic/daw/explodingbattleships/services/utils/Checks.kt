package pt.isel.leic.daw.explodingbattleships.services.utils

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.*

/**
 * Throws an [AppException] if the undesired condition is verified
 * @param undesiredCondition the undesired condition
 * @param errorMessage the error message to be thrown
 */
fun checkOrThrowBadRequest(undesiredCondition: Boolean, errorMessage: String) {
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
    checkOrThrowBadRequest(limit <= 0, "Invalid limit")
    checkOrThrowBadRequest(skip < 0, "Invalid skip")
}

/**
 * Checks if a password is valid and
 * throws an exception if it is not
 * @param password the password
 */
fun checkPasswordValid(password: String) {
    checkOrThrowBadRequest(!password.any { it.isDigit() }, "Password doesn't contain numbers")
    checkOrThrowBadRequest(!password.any { it.isUpperCase() }, "Password doesn't contain uppercase letters")
    checkOrThrowBadRequest(!password.any { it.isLowerCase() }, "Password doesn't contain lowercase letters")
}

fun checkShipLayout(gameTypeName: String, ships: List<UnverifiedShip>): List<VerifiedShip> {
    val gameType = gameTypeName.toGameType()
        ?: throw AppException("Game type not registered")
    checkOrThrowBadRequest(
        ships.size != gameType.fleetComposition.size,
        "Can only place ${gameType.fleetComposition.size} ships"
    )
    checkOrThrowBadRequest(
        !shipsValid(gameType, ships),
        "Invalid ship list for ${gameType.name} game"
    )
    val occupiedSquares = mutableSetOf<VerifiedSquare?>()
    val verifiedShips = mutableListOf<VerifiedShip>()
    ships.forEach { unverifiedShip ->
        val verifiedShip = unverifiedShip.toVerifiedShipOrNull(gameType)
            ?: throw AppException("Invalid ship", AppExceptionStatus.BAD_REQUEST)
        when (verifiedShip.orientation.lowercase()) {
            "vertical" -> validateShipSquares(verifiedShip, gameType.boardSize, occupiedSquares, VerifiedSquare::down)
            "horizontal" -> validateShipSquares(verifiedShip, gameType.boardSize, occupiedSquares, VerifiedSquare::right)
            else -> throw AppException("Invalid orientation for ${verifiedShip.name}", AppExceptionStatus.BAD_REQUEST)
        }
        verifiedShips.add(verifiedShip)
    }
    return verifiedShips
}

/**
 * Checks if a list of ships is a valid one
 * @param gameType the type of game
 * @param ships the list of ships
 * @return true if the list is valid
 */
private fun shipsValid(gameType: GameType, ships: List<UnverifiedShip>) =
    ships.map { it.name }.containsAll(gameType.fleetComposition.map { it.name })

/**
 * Checks if a square is within a board
 * @param square the square in question
 * @param boardSize the size of the board
 */
fun squareInBoard(square: VerifiedSquare, boardSize: Int): Boolean {
    val lastRow = 'a' + boardSize - 1
    val lastColumn = 1 + boardSize - 1
    if (square.row !in 'a'..lastRow) return false
    if (square.column !in 1..lastColumn) return false
    return true
}

/**
 * Validates the squares of a [UnverifiedShip]
 * @param ship the ship in question
 * @param occupiedSquares the occupied squares
 * @param nextSquare the function to calculate the next square
 */
private fun validateShipSquares(ship: VerifiedShip, boardSize: Int, occupiedSquares: MutableSet<VerifiedSquare?>, nextSquare: NextSquare) {
    var currentSquare = ship.firstSquare
    for (i in 0 until ship.size) {
        checkOrThrowBadRequest(!squareInBoard(currentSquare, boardSize), "Invalid square on ${currentSquare.getString()}")
        checkOrThrowBadRequest(occupiedSquares.contains(currentSquare), "Square already occupied on ${currentSquare.getString()}")
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
fun computePlayer(transaction: Transaction, token: String?, data: Data): PlayerOutputModel {
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
    checkOrThrowBadRequest(gameState != state, "Invalid game state")

fun checkPlayerInGame(game: Game, playerId: Int) =
    checkOrThrowBadRequest(game.player1 != playerId && game.player2 != playerId, "Player not in game")

fun checkCurrentPlayer(game: Game, playerId: Int) =
    checkOrThrowBadRequest(game.currPlayer != playerId, "Not your turn")
