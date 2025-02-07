package pt.isel.leic.daw.explodingbattleships.services.utils

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.FullGameInfo
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.GameType
import pt.isel.leic.daw.explodingbattleships.domain.GameTypeWithFleet
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.ShipCreationInfo
import pt.isel.leic.daw.explodingbattleships.domain.ShipType
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.domain.Token
import pt.isel.leic.daw.explodingbattleships.domain.UserInfo
import pt.isel.leic.daw.explodingbattleships.domain.toSquare
import pt.isel.leic.daw.explodingbattleships.services.UsersServices
import java.time.Instant
import java.util.regex.Pattern

/**
 * Checks if a token is still valid
 * @param token the token
 * @return true if the token is valid
 */
fun isTokenStillValid(token: Token): Boolean {
    val now = Instant.now()
    return now.isBefore(token.createdAt.plus(UsersServices.TOKEN_TTL)) &&
        now.isBefore(token.lastUsedAt.plus(UsersServices.TOKEN_ROLLING_TTL))
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
 * Checks if a list of ships is valid
 * @param fleetComposition the fleet composition of the game type
 * @param ships the ships list the user is creating
 * @return true if the ships list is valid
 */
fun shipsValid(fleetComposition: List<ShipType>, ships: List<ShipCreationInfo>) =
    ships.map { it.name }.containsAll(fleetComposition.map { it.name })

/**
 * Responsible for executing the hits
 * @param transaction the current transaction
 * @param game the current game
 * @param squares the squares that will be hit
 * @param playerId the player id
 * @param data the data module to be used
 */
fun executeHits(transaction: Transaction, game: Game, squares: List<Square>, playerId: Int, data: Data) {
    val shipsSquares = data.shipsData.getShipsAndSquares(transaction, game.id, playerId)
    squares.forEach { square ->
        val entry = shipsSquares.entries.find { it.value.contains(square) }
        if (entry != null) {
            data.hitsData.createHit(transaction, square, game.id, playerId, true)
            data.shipsData.updateNumOfHits(transaction, game.id, playerId, entry.key.firstSquare)
            maybeDestroyShip(transaction, playerId, game.id, entry.key, data)
        } else {
            data.hitsData.createHit(transaction, square, game.id, playerId, false)
        }
    }
}

/**
 * Checks if a ship was destroyed this turn or not
 * @param transaction the current transaction
 * @param userId the user id
 * @param gameId the game id
 * @param ship the ship
 * @param data the data module to be used
 */
fun maybeDestroyShip(transaction: Transaction, userId: Int, gameId: Int, ship: Ship, data: Data) {
    val nOfHits = data.shipsData.getShip(transaction, ship.firstSquare, gameId, userId)?.nOfHits
    if (ship.size == nOfHits) {
        data.shipsData.destroyShip(transaction, gameId, userId, ship.firstSquare)
    }
}

/**
 * Checks if the enemy's fleet is destroyed
 */
fun winConditionDetection(transaction: Transaction, gameId: Int, playerId: Int, data: Data): Boolean =
    data.shipsData.getFleet(transaction, gameId, playerId).let { fleet ->
        fleet.isNotEmpty() && fleet.all { it.destroyed }
    }

/**
 * Checks if the time is over for a player to play
 * @param game the game
 * @return true if the time is over
 */
fun GameTypeWithFleet.isTimeOver(game: Game): Boolean {
    if (game.state == "shooting") {
        return game.startedAt.plusSeconds(shootingTimeInSecs.toLong()) <= Instant.now()
    }
    if (game.state == "layout_definition") {
        return game.startedAt.plusSeconds(layoutDefTimeInSecs.toLong()) <= Instant.now()
    }
    return false
}

/**
 * Gets a game type
 * @param transaction the current transaction
 * @param gameType the game type name
 * @param data the data module to be used
 * @return the [GameType]
 */
fun getGameType(transaction: Transaction, gameType: String, data: Data) =
    data.gameTypesData.getGameType(transaction, gameType)
        ?: throw AppException("Invalid game type", "Game type doesn't exist", AppExceptionStatus.BAD_REQUEST)

/**
 * Changes the current player if the current player exceeded their time
 * @param transaction the current transaction
 * @param gameId the game id
 * @param data the data module to be used
 * @return a new game with a new current player or the original game
 */
fun computeGame(transaction: Transaction, gameId: Int, data: Data): Game {
    val game = getGameOrThrow(transaction, gameId, data)
    val gameType = getGameType(transaction, game.type, data)
    if (game.state == "layout_definition" && gameType.isTimeOver(game)) {
        data.gamesData.setGameStateCompleted(transaction, game.id)
        return getData { data.gamesData.getGame(transaction, game.id) }
    }
    if (game.state == "shooting" && gameType.isTimeOver(game)) {
        data.gamesData.changeCurrPlayer(transaction, game.id, game.idlePlayer())
        return getData { data.gamesData.getGame(transaction, game.id) }
    }
    return game
}

/**
 * Gets a full game information
 * @param transaction the current transaction
 * @param game the game
 */
fun getFullGameInfo(transaction: Transaction, game: Game, userId: Int, data: Data): FullGameInfo {
    val gameType = getData { data.gameTypesData.getGameType(transaction, game.type) }
    val opponent = game.otherPlayer(userId).let { opponentId ->
        val user = getData { data.usersData.getUserById(transaction, opponentId) }
        UserInfo(user.id, user.name, user.score)
    }
    val playing = game.currPlayer == userId
    val playerFleet = data.shipsData.getFleet(transaction, game.id, userId)
    val takenHitsSquares = data.hitsData.getHits(transaction, game.id, userId).map { it.square.toSquare() }
    val enemyFleet = data.shipsData.getFleet(transaction, game.id, opponent.id).filter { it.destroyed }
    val sentHits = data.hitsData.getHits(transaction, game.id, opponent.id)
    val hits = sentHits.filter { it.onShip }.map { it.square.toSquare() }
    val misses = sentHits.filter { !it.onShip }.map { it.square.toSquare() }
    return FullGameInfo(
        game,
        gameType,
        opponent,
        playing,
        playerFleet,
        takenHitsSquares,
        enemyFleet,
        hits,
        misses
    )
}