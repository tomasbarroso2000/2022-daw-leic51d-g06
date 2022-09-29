package pt.isel.leic.daw.explodingbattleships.services.comp.utils

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Ship
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
 * Sends the token to getPlayerIdByToken in the data module
 * @param token the token linked to the player
 * @param data the data module
 * @return [Int] the id of the player
 */
fun getPlayerId(transaction: Transaction, token: String?, data: Data): Int {
    if (token == null || token == "") throw AppException("No token provided", AppExceptionStatus.UNAUTHORIZED)
    return data.playerData.getPlayerIdByToken(transaction, token)
        ?: throw AppException("Invalid token", AppExceptionStatus.UNAUTHORIZED)
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
 * Checks if the limit and skip values are valid and
 * throws an exception they are not
 * @param limit the given limit
 * @param skip the given skip
 */
fun checkLimitAndSkip(limit: Int, skip: Int) {
    if (limit <= 0)
        throw AppException("Invalid limit", AppExceptionStatus.BAD_REQUEST)
    if (skip < 0)
        throw AppException("Invalid skip", AppExceptionStatus.BAD_REQUEST)
}

private fun checkShipLayout(ships: List<Ship>, width: Int, height: Int) {
    if (ships.size != 5)
        throw AppException("Can only place 5 ships", AppExceptionStatus.BAD_REQUEST)

}
