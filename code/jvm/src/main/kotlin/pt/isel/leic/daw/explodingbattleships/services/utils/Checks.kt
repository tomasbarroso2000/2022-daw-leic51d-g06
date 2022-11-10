package pt.isel.leic.daw.explodingbattleships.services.utils

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.GameType
import pt.isel.leic.daw.explodingbattleships.domain.NextSquare
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.ShipCreationInfo
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.domain.down
import pt.isel.leic.daw.explodingbattleships.domain.getString
import pt.isel.leic.daw.explodingbattleships.domain.left
import pt.isel.leic.daw.explodingbattleships.domain.right
import pt.isel.leic.daw.explodingbattleships.domain.toShipOrNull
import pt.isel.leic.daw.explodingbattleships.domain.toSquareOrNull
import pt.isel.leic.daw.explodingbattleships.domain.up
import java.util.regex.Pattern

/**
 * Throws an [AppException] if the undesired condition is verified
 * @param undesiredCondition the undesired condition
 * @param errorMessage the error message to be thrown
 */
fun checkOrThrowBadRequest(undesiredCondition: Boolean, errorMessage: String) {
    if (undesiredCondition) {
        throw AppException(errorMessage, AppExceptionStatus.BAD_REQUEST)
    }
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
 * Check if the email address is valid
 * @param email the given email
 * @return if the email is valid
 */
fun isEmailValid(email: String): Boolean {
    return Pattern.compile("^(.+)@(\\S+)$")
        .matcher(email)
        .matches()
}

/**
 * Checks if an email is valid and
 * throws an exception if it is not
 * @param email the given email
 */
fun checkEmailValid(email: String) {
    checkOrThrowBadRequest(!isEmailValid(email), "Invalid email format")
}

/**
 * Checks if a password is valid and
 * throws an exception if it is not
 * @param password the given password
 */
fun checkPasswordValid(password: String) {
    checkOrThrowBadRequest(!password.any { it.isDigit() }, "Password doesn't contain numbers")
    checkOrThrowBadRequest(!password.any { it.isUpperCase() }, "Password doesn't contain uppercase letters")
    checkOrThrowBadRequest(!password.any { it.isLowerCase() }, "Password doesn't contain lowercase letters")
}

/**
 * Checks if a layout is valid and
 * throws an exception if it is not
 * @param userId the user id
 * @param game the game
 * @param ships a list of information needed to create the ships
 * @return a ships list
 */
fun checkShipLayout(userId: Int, game: Game, ships: List<ShipCreationInfo>): List<Ship> {
    val gameType = game.type.toGameTypeOrNull()
        ?: throw AppException("Game type not registered")
    checkOrThrowBadRequest(
        ships.size != gameType.fleetComposition.size,
        "Can only place ${gameType.fleetComposition.size} ships"
    )
    checkOrThrowBadRequest(
        !shipsValid(gameType, ships),
        "Invalid ship list for ${gameType.name} game"
    )
    val unavailableSquares = mutableSetOf<Square>()
    val verifiedShips = mutableListOf<Ship>()
    ships.forEach { unverifiedShip ->
        val verifiedShip = unverifiedShip.toShipOrNull(userId, game.id, gameType)
            ?: throw AppException("Invalid ship", AppExceptionStatus.BAD_REQUEST)
        when (verifiedShip.orientation.lowercase()) {
            "vertical" -> validateShipSquares(verifiedShip, gameType.boardSize, unavailableSquares, Square::down)
            "horizontal" -> validateShipSquares(verifiedShip, gameType.boardSize, unavailableSquares, Square::right)
            else -> throw AppException("Invalid orientation for ${verifiedShip.name}", AppExceptionStatus.BAD_REQUEST)
        }
        verifiedShips.add(verifiedShip)
    }
    return verifiedShips
}

/**
 * Checks if a list of ships is valid
 * @param gameType the game type
 * @param ships the ships list
 * @return true if the ships list is valid
 */
private fun shipsValid(gameType: GameType, ships: List<ShipCreationInfo>) =
    ships.map { it.name }.containsAll(gameType.fleetComposition.map { it.name })

/**
 * Checks if a square is within a board
 * @param square the square in question
 * @param boardSize the size of the board
 * @return if a square is within a board
 */
fun squareInBoard(square: Square, boardSize: Int): Boolean {
    val lastRow = 'a' + boardSize - 1
    val lastColumn = 1 + boardSize - 1
    if (square.row !in 'a'..lastRow) return false
    if (square.column !in 1..lastColumn) return false
    return true
}

/**
 * Validates the squares of a ship
 * @param ship the ship
 * @param unavailableSquares the unavailable squares
 * @param nextSquare the function to calculate the next square
 */
private fun validateShipSquares(
    ship: Ship,
    boardSize: Int,
    unavailableSquares: MutableSet<Square>,
    nextSquare: NextSquare
) {
    var currentSquare = ship.firstSquare.toSquareOrNull()
        ?: throw AppException("Invalid first square: ${ship.firstSquare}")
    val shipSquares = mutableListOf<Square>()
    repeat(ship.size) {
        checkOrThrowBadRequest(
            !squareInBoard(currentSquare, boardSize),
            "Invalid square: ${currentSquare.getString()}"
        )
        checkOrThrowBadRequest(
            unavailableSquares.contains(currentSquare),
            "Can't place on this square: ${currentSquare.getString()}"
        )

        shipSquares.add(currentSquare)
        currentSquare = currentSquare.nextSquare()
    }
    // needs cleaning up
    val surroundingSquares = mutableListOf<Square>()
    shipSquares.forEach { shipSquare ->
        surroundingSquares.add(shipSquare.up().left())
        surroundingSquares.add(shipSquare.up())
        surroundingSquares.add(shipSquare.up().right())
        surroundingSquares.add(shipSquare.left())
        surroundingSquares.add(shipSquare.right())
        surroundingSquares.add(shipSquare.down().left())
        surroundingSquares.add(shipSquare.down())
        surroundingSquares.add(shipSquare.down().right())
    }
    unavailableSquares.addAll(shipSquares)
    unavailableSquares.addAll(surroundingSquares)
}

/**
 * Gets the game corresponding to the game id
 * @param transaction the transaction that will be used to obtain the game
 * @param gameId game id
 * @param data specifies what section of data is accessed
 * @return the game
 */
fun computeGame(transaction: Transaction, gameId: Int, data: Data): Game {
    if (gameId <= 0) {
        throw AppException("Invalid game id", AppExceptionStatus.BAD_REQUEST)
    }
    return data.gamesData.getGame(transaction, gameId)
        ?: throw AppException("Game does not exist", AppExceptionStatus.NOT_FOUND)
}

/**
 * Checks if a game state is valid and
 * throws an exception if it is not
 * @param gameState the current game state
 * @param state the desired game state
 */
fun checkGameState(gameState: String, state: String) =
    checkOrThrowBadRequest(gameState != state, "Invalid game state")

/**
 * Checks if a player is present in a game
 * throws an exception if it is not
 * @param game the given game
 * @param playerId the player id
 */
fun checkPlayerInGame(game: Game, playerId: Int) =
    checkOrThrowBadRequest(game.player1 != playerId && game.player2 != playerId, "Player not in game")

/**
 * Checks if a player's turn is valid
 * throws an exception if it is not
 * @param game the given game
 * @param playerId the player id
 */
fun checkCurrentPlayer(game: Game, playerId: Int) =
    checkOrThrowBadRequest(game.currPlayer != playerId, "Not your turn")