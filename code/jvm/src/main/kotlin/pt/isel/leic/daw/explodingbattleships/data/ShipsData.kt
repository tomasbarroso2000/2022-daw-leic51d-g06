package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.Square

interface ShipsData {
    /**
     * Defines the ships layout for the game
     * @param transaction the current transaction
     * @param gameId the game id
     * @param userId the user id
     * @param ships the list of ships to be placed
     */
    fun defineLayout(transaction: Transaction, gameId: Int, userId: Int, ships: List<Ship>)

    /**
     * Checks if the enemy layout is done
     * @param transaction the current transaction
     * @param gameId the game id
     * @param userId the user id
     * @return if the enemy layout is done
     */
    fun checkEnemyLayoutDone(transaction: Transaction, gameId: Int, userId: Int): Boolean

    /**
     * Gets the users ships and their corresponding squares
     * @param transaction the current transaction
     * @param gameId the game id
     * @param userId the user id
     * @return a map with the ships and their corresponding squares
     */
    fun getShipsAndSquares(transaction: Transaction, gameId: Int, userId: Int): Map<Ship, Set<Square>>

    /**
     * Updates the number of hits a ship has taken
     * @param transaction the current transaction
     * @param gameId the game id
     * @param userId the user id
     * @param firstSquare the first square of the ship taking the hit
     */
    fun updateNumOfHits(transaction: Transaction, gameId: Int, userId: Int, firstSquare: String)

    /**
     * Gets the fleet of a user
     * @param transaction the current transaction
     * @param gameId the game id
     * @param userId the user id
     */
    fun getFleet(transaction: Transaction, gameId: Int, userId: Int): List<Ship>

    /**
     * Gets the ship with the corresponding first square
     * @param transaction the current transaction
     * @param firstSquare the ships first square
     * @param gameId the game id
     * @param userId the user id
     * @return the ship
     */
    fun getShip(transaction: Transaction, firstSquare: String, gameId: Int, userId: Int): Ship?

    /**
     * Destroys a ship with the corresponding first square
     * @param transaction the current transaction
     * @param gameId the game id
     * @param userId the user id
     * @param firstSquare the ships first square
     */
    fun destroyShip(transaction: Transaction, gameId: Int, userId: Int, firstSquare: String)

    /**
     * Verifies if the user has ships
     * @param transaction the current transaction
     * @param userId the user id
     * @param gameId the game id
     * @return if the user has ships
     */
    fun hasShips(transaction: Transaction, userId: Int, gameId: Int): Boolean
}
