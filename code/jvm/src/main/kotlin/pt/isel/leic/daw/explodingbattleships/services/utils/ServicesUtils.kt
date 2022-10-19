package pt.isel.leic.daw.explodingbattleships.services.utils

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.*
import java.time.Duration
import java.time.Instant
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
 * Check if the email address is valid
 */
fun isEmailValid(email: String): Boolean {
    return Pattern.compile("^(.+)@(\\S+)$")
        .matcher(email)
        .matches()
}

/**
 * Returns the game the player is playing
 * @param transaction the current transaction
 * @param playerId the player id
 * @param data the data module to be used
 * @return the game that the player is playing
 */
fun getPlayerGameOrThrow(transaction: Transaction, playerId: Int, data: Data): Game =
    data.gamesData.getPlayerGame(transaction, playerId)
        ?: throw AppException("Player not in a game", AppExceptionStatus.BAD_REQUEST)

/**
 * Returns whether the player is in a game or not
 * @param transaction the current transaction
 * @param playerId the player id
 * @param data the data module to be used
 * @return true if the player is in a game
 */
fun isPlayerInAGame(transaction: Transaction, playerId: Int, data: Data): Boolean =
    data.gamesData.getPlayerGame(transaction, playerId) != null

/**
 * Returns whether the player is already in a lobby
 * @param transaction the current transaction
 * @param playerId the player id
 * @param data the data module to be used
 * @return true if the player is already in a lobby
 */
fun isPlayerInLobby(transaction: Transaction, playerId: Int, data: Data): Boolean =
    data.playersData.isPlayerInLobby(transaction, playerId)

/**
 * Responsible for executing the hit, producing a list with the hits outcome
 * and if any ship was destroyed
 * @param transaction the current transaction
 * @param game the current game
 * @param verifiedSquares the squares that will be hit
 * @param playerId the player id
 * @param data the data module to be used
 * @return a list with the hits outcome
 */
fun executeHit(transaction: Transaction, game: Game, verifiedSquares: List<VerifiedSquare>, playerId: Int, data: Data): HitsOutcome {
    val shipsSquares = data.inGameData.getShipAndSquares(transaction, game.id, playerId)
    val hitOutcomeList = mutableListOf<HitOutcome>()
    verifiedSquares.forEach { square ->
        if (!data.inGameData.createHit(transaction, square, game.id, playerId))
            throw AppException("Unsuccessful hit")
        val entry = shipsSquares.entries.find { it.value.contains(square) }
        if (entry != null) {
            if (!data.inGameData.updateNumOfHits(transaction, game.id, playerId, entry.key.firstSquare.getString()))
                throw AppException("Unsuccessful hit")
            val destroyed = maybeDestroyShip(transaction, playerId, game.id, entry.key, data)
            if (destroyed) hitOutcomeList.add(HitOutcome(square, true, entry.key.name))
            else hitOutcomeList.add(HitOutcome(square, true))
        } else {
            hitOutcomeList.add(HitOutcome(square, false))
        }
    }
    if (winConditionDetection(transaction, game.id, playerId, data))
        return HitsOutcome(hitOutcomeList, true)
    if (!data.gamesData.changeCurrPlayer(transaction, game.id, game.idlePlayer()))
        throw AppException("Unsuccessful hit")
    return HitsOutcome(hitOutcomeList, false)
}

/**
 *  Get the number of hits of a ship
 *  @param transaction the current transaction
 */
fun getNumberOfHits(transaction: Transaction, gameId: Int, playerId: Int, verifiedShip: VerifiedShip, data: Data): Int =
    data.inGameData.getNumOfHits(transaction, verifiedShip.firstSquare, gameId, playerId)


/**
 * Checks if a ship was destroyed this turn or not
 */
fun maybeDestroyShip(transaction: Transaction, playerId: Int, gameId: Int, ship: VerifiedShip, data: Data): Boolean {
    val nOfHits = getNumberOfHits(transaction, gameId, playerId, ship, data)
    if (ship.size == nOfHits) {
        if (!data.inGameData.destroyShip(transaction, gameId, playerId, ship.firstSquare))
            throw AppException("Error destroying ship")
        return true
    }
    return false
}

/**
 * Checks if player won
 */
fun winConditionDetection(transaction: Transaction, gameId: Int, playerId: Int, data: Data): Boolean =
    data.inGameData.fleetState(transaction, gameId, playerId).all { it.destroyed == true }


/**
 * Checks if a game type exists in the system
 * @param gameType the game type name
 * @return true if the game type exists
 */
fun isGameTypeInvalid(gameType: String) = gameType.toGameType() == null

/**
 * Inserts in lobby or creates a game
 * @param transaction the current transaction
 * @param playerId the player id
 * @param gameType the game type
 * @param data the data module to be used
 * @return information about whether the player was placed in the lobby or a game was started
 */
fun enterLobbyOrCreateGame(transaction: Transaction, playerId: Int, gameType: String, data: Data): EnterLobbyOutput {
    val matchingLobby = data.playersData.searchLobbies(transaction, gameType).firstOrNull()
    if (matchingLobby != null) {
        data.playersData.removeLobby(transaction, matchingLobby.player, matchingLobby.gameType, matchingLobby.enterTime)
        return data.gamesData.createGame(transaction, gameType, playerId, matchingLobby.player, Instant.now())
            .let { EnterLobbyOutput(false, it) }
    }
    return data.playersData.enterLobby(transaction, playerId, gameType)
}

fun String.toGameType() = GameType.values().find { it.name == this.uppercase() }
