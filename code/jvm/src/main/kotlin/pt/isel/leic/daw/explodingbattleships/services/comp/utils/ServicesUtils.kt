package pt.isel.leic.daw.explodingbattleships.services.comp.utils

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.HitOutcome
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare
import java.util.regex.Pattern

const val BOARD_MIN_HEIGHT = 10
const val BOARD_MIN_WIDTH = 10

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
 * Returns the game id of the game the player is playing
 * @param transaction the current transaction
 * @param playerId the player id
 * @param data the data module to be used
 * @return the game that the player is playing
 */
fun getPlayerGame(transaction: Transaction, playerId: Int, data: Data): Game? =
    data.gamesData.getPlayerGame(transaction, playerId)

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
 * and if any ship was detroyed
 * @param transaction the current transaction
 * @param game the current game
 * @param verifiedSquares the squares that will be hit
 * @param playerId the player id
 * @param data the data module to be used
 * @return a list with the hits outcome
 */
fun executeHit(transaction: Transaction, game: Game, verifiedSquares: MutableList<VerifiedSquare>, playerId: Int, data: Data) : MutableList<HitOutcome> {
    val shipsSquares = data.inGameData.getShipAndSquares(transaction, game.id, playerId)
    val hitOutcomeList = mutableListOf<HitOutcome>()
    verifiedSquares.forEach { square ->
        if (data.inGameData.createHit(transaction, square, game.id, playerId) != 1)
            throw AppException("Unsuccessful hit")
        val entry = shipsSquares.entries.find { it.value.contains(square) }
        if (entry != null) {
            if (data.inGameData.updateNumOfHits(transaction, game.id, playerId, entry.key.name) != 1)
                throw AppException("Unsuccessful hit")
            val destroyed = data.inGameData.isShipDestroyed(transaction, game.id, playerId, entry.key.name)
            if (destroyed)
                hitOutcomeList.add(HitOutcome(square, true, entry.key.name))
            else
                hitOutcomeList.add(HitOutcome(square, true))
        } else {
            hitOutcomeList.add(HitOutcome(square, false))
        }
    }
    return hitOutcomeList
}