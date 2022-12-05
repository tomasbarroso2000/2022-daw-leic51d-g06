package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.DataList
import pt.isel.leic.daw.explodingbattleships.domain.User
import pt.isel.leic.daw.explodingbattleships.domain.UserInfo

interface UsersData {

    /**
     * Gets a user by their id
     * @param transaction the current transaction
     * @param userId the user id
     * @return the found [User] or null
     */
    fun getUserById(transaction: Transaction, userId: Int): User?

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
     * @param passwordVer the new user's hashed password
     * @return the user id
     */
    fun createUser(transaction: Transaction, name: String, email: String, passwordVer: String): Int

    /**
     * Gets the users rankings
     * @param transaction the current transaction
     * @param limit the last index of the database table to be fetched
     * @param skip the first index of the database table to be fetched
     * @return a list of rankings and if there are more ranked users in the database
     */
    fun getRankings(transaction: Transaction, limit: Int, skip: Int): DataList<UserInfo>

    /**
     * Increases the player score
     * @param transaction the current transaction
     * @param userId the user id
     */
    fun increasePlayerScore(transaction: Transaction, userId: Int)
}