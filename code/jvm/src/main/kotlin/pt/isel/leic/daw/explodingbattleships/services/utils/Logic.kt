package pt.isel.leic.daw.explodingbattleships.services.utils

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.GameType
import pt.isel.leic.daw.explodingbattleships.domain.HitOutcome
import pt.isel.leic.daw.explodingbattleships.domain.HitsOutcome
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.ShipCreationInfo
import pt.isel.leic.daw.explodingbattleships.domain.ShipType
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.domain.Token
import pt.isel.leic.daw.explodingbattleships.services.UsersServices
import java.lang.IllegalArgumentException
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
 * Responsible for executing the hit, producing a list with the hits outcome
 * and if any ship was destroyed
 * @param transaction the current transaction
 * @param game the current game
 * @param squares the squares that will be hit
 * @param playerId the player id
 * @param data the data module to be used
 * @return a list with the hits outcome
 */
fun executeHit(transaction: Transaction, game: Game, squares: List<Square>, playerId: Int, data: Data): HitsOutcome {
    val shipsSquares = data.shipsData.getShipsAndSquares(transaction, game.id, playerId)
    val hitOutcomeList = mutableListOf<HitOutcome>()
    squares.forEach { square ->
        val entry = shipsSquares.entries.find { it.value.contains(square) }
        if (entry != null) {
            data.hitsData.createHit(transaction, square, game.id, playerId, true)
            data.shipsData.updateNumOfHits(transaction, game.id, playerId, entry.key.firstSquare)
            val destroyed = maybeDestroyShip(transaction, playerId, game.id, entry.key, data)
            if (destroyed) {
                hitOutcomeList.add(HitOutcome(square, true, entry.key.name))
            } else {
                hitOutcomeList.add(HitOutcome(square, true))
            }
        } else {
            data.hitsData.createHit(transaction, square, game.id, playerId, false)
            hitOutcomeList.add(HitOutcome(square, false))
        }
    }
    if (winConditionDetection(transaction, game.id, playerId, data)) {
        return HitsOutcome(hitOutcomeList, true)
    }
    data.gamesData.changeCurrPlayer(transaction, game.id, game.idlePlayer())
    return HitsOutcome(hitOutcomeList, false)
}

/**
 * Checks if a ship was destroyed this turn or not
 */
fun maybeDestroyShip(transaction: Transaction, userId: Int, gameId: Int, ship: Ship, data: Data): Boolean {
    val nOfHits = data.shipsData.getShip(transaction, ship.firstSquare, gameId, userId)?.nOfHits
    if (ship.size == nOfHits) {
        data.shipsData.destroyShip(transaction, gameId, userId, ship.firstSquare)
        return true
    }
    return false
}

/**
 * Checks if player won
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
fun GameType.isTimeOver(game: Game) =
    game.startedAt.plusSeconds(shootingTimeInSecs.toLong()) <= Instant.now()

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
    if (game.state == "shooting" && gameType.isTimeOver(game)) {
        data.gamesData.changeCurrPlayer(transaction, game.id, game.idlePlayer())
        return data.gamesData.getGame(transaction, game.id)
            ?: throw IllegalArgumentException("Get game is null")
    }
    return game
}