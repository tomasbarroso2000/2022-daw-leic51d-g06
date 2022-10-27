package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.DataList
import pt.isel.leic.daw.explodingbattleships.domain.Ranking
import pt.isel.leic.daw.explodingbattleships.domain.User

interface UsersData {
    /**
     * Gets the user with the corresponding token
     * @param transaction the current transaction
     * @token the token
     * @return the user or null if there is no user with the token
     */
    fun getUserFromToken(transaction: Transaction, token: String): User?

    /**
     * Gets the user with the corresponding email
     * @param transaction the current transaction
     * @param email the email
     * @return the user or null if there is no user with the email
     */
    fun getUserFromEmail(transaction: Transaction, email: String): User?

    /**
     * Creates a user
     * @param transaction the current transaction
     * @param name the new user's name
     * @param email the new user's email
     * @param password the new user's password
     * @return the user id
     */
    fun createUser(transaction: Transaction, name: String, email: String, password: Int): Int

    /**
     * Creates a token for a user
     * @param transaction the current transaction
     * @param userId the user id
     * @return the token
     */
    fun createToken(transaction: Transaction, userId: Int): String

    /**
     * Gets the users rankings
     * @param transaction the current transaction
     * @param limit the last index of the database table to be fetched
     * @param skip the first index of the database table to be fetched
     * @retun a list of rankings and if there are more ranked users in the database
     */
    fun getRankings(transaction: Transaction, limit: Int, skip: Int): DataList<Ranking>
}
