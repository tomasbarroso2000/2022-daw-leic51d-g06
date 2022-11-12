package pt.isel.leic.daw.explodingbattleships.services.utils

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.ShipCreationInfo
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.domain.toSquareOrNull

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
    checkOrThrowBadRequest(password.length < 4, "Password needs to be at least 4 characters")
    checkOrThrowBadRequest(!password.any { it.isDigit() }, "Password doesn't contain numbers")
    checkOrThrowBadRequest(!password.any { it.isUpperCase() }, "Password doesn't contain uppercase letters")
    checkOrThrowBadRequest(!password.any { it.isLowerCase() }, "Password doesn't contain lowercase letters")
}

/**
 * Checks if a layout is valid and
 * throws an exception if it is not
 * @param transaction the current transaction
 * @param userId the user id
 * @param game the game
 * @param ships a list of information needed to create the ships
 * @param data the data module to be used
 * @return a ships list
 */
fun checkShipLayout(
    transaction: Transaction,
    userId: Int,
    game: Game,
    ships: List<ShipCreationInfo>,
    data: Data
): List<Ship> {
    val gameType = getGameType(transaction, game.type, data)
    val fleetComposition = data.shipTypesData.getGameTypeShips(transaction, gameType)
    checkOrThrowBadRequest(
        ships.size != fleetComposition.size,
        "Can only place ${fleetComposition.size} ships"
    )
    checkOrThrowBadRequest(
        !shipsValid(fleetComposition, ships),
        "Invalid ship list for ${gameType.name} game"
    )
    val unavailableSquares = mutableSetOf<Square>()
    val verifiedShips = mutableListOf<Ship>()
    ships.forEach { unverifiedShip ->
        val verifiedShip = unverifiedShip.toShipOrNull(userId, game.id, fleetComposition)
            ?: throw AppException("Invalid ship", AppExceptionStatus.BAD_REQUEST)
        when (verifiedShip.orientation.lowercase()) {
            "vertical" -> checkShipSquares(verifiedShip, gameType.boardSize, unavailableSquares, Square::down)
            "horizontal" -> checkShipSquares(verifiedShip, gameType.boardSize, unavailableSquares, Square::right)
            else -> throw AppException("Invalid orientation for ${verifiedShip.name}", AppExceptionStatus.BAD_REQUEST)
        }
        verifiedShips.add(verifiedShip)
    }
    return verifiedShips
}

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
 * Checks the squares of a ship
 * @param ship the ship
 * @param unavailableSquares the unavailable squares
 * @param nextSquare the function to calculate the next square
 */
private fun checkShipSquares(
    ship: Ship,
    boardSize: Int,
    unavailableSquares: MutableSet<Square>,
    nextSquare: Square.() -> Square
) {
    var currentSquare = ship.firstSquare.toSquareOrNull()
        ?: throw AppException("Invalid first square: ${ship.firstSquare}")
    val shipSquares = mutableListOf<Square>()
    repeat(ship.size) {
        checkOrThrowBadRequest(
            !squareInBoard(currentSquare, boardSize),
            "Invalid square: $currentSquare"
        )
        checkOrThrowBadRequest(
            unavailableSquares.contains(currentSquare),
            "Can't place on this square: $currentSquare"
        )

        shipSquares.add(currentSquare)
        currentSquare = currentSquare.nextSquare()
    }
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
fun getGameOrThrow(transaction: Transaction, gameId: Int, data: Data): Game {
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