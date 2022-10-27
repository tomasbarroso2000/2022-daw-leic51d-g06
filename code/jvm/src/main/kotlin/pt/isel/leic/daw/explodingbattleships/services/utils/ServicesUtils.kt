package pt.isel.leic.daw.explodingbattleships.services.utils

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.GameType
import pt.isel.leic.daw.explodingbattleships.domain.HitOutcome
import pt.isel.leic.daw.explodingbattleships.domain.HitsOutcome
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.domain.idlePlayer

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
        if (error is AppException) {
            throw error
        } else {
            throw handleDataError(error)
        }
    }
}

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
fun maybeDestroyShip(transaction: Transaction, playerId: Int, gameId: Int, ship: Ship, data: Data): Boolean {
    val nOfHits = data.shipsData.getShip(transaction, ship.firstSquare, gameId, playerId)?.nOfHits
    if (ship.size == nOfHits) {
        data.shipsData.destroyShip(transaction, gameId, playerId, ship.firstSquare)
        return true
    }
    return false
}

/**
 * Checks if player won
 */
fun winConditionDetection(transaction: Transaction, gameId: Int, playerId: Int, data: Data): Boolean =
    data.shipsData.getFleet(transaction, gameId, playerId).all { it.destroyed }

/**
 * Checks if a game type exists in the system
 * @param gameType the game type name
 * @return true if the game type exists
 */
fun isGameTypeInvalid(gameType: String) = gameType.toGameTypeOrNull() == null

/**
 * Inserts in lobby or creates a game
 * @param transaction the current transaction
 * @param playerId the player id
 * @param gameType the game type
 * @param data the data module to be used
 * @return information about whether the player was placed in the lobby or a game was started
 */
fun enterLobbyOrCreateGame(transaction: Transaction, playerId: Int, gameType: String, data: Data): Int? {
    val matchingLobby = data.lobbiesData.searchLobbies(transaction, gameType, playerId).firstOrNull()
    if (matchingLobby != null) {
        data.lobbiesData.removeLobby(transaction, matchingLobby.userId, matchingLobby.gameType, matchingLobby.enterTime)
        return data.gamesData.createGame(transaction, gameType, playerId, matchingLobby.userId)
    }
    data.lobbiesData.enterLobby(transaction, playerId, gameType)
    return null
}

/**
 * Converts the string to one of the game types or null
 */
fun String.toGameTypeOrNull() = GameType.values().find { it.name == this.uppercase() }

/**
 * Converts the string to a game type or throws an exception
 */
fun String.toGameTypeOrThrow() = toGameTypeOrNull() ?: throw IllegalArgumentException("Invalid game type")