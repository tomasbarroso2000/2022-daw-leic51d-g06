package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.Hit
import pt.isel.leic.daw.explodingbattleships.domain.Square

interface HitsData {
    /**
     * Creates a hit
     * @param transaction the current transaction
     * @param square the square being hit
     * @param gameId the game id
     * @param userId the user id
     * @param onShip if it hit a ship
     */
    fun createHit(transaction: Transaction, square: Square, gameId: Int, userId: Int, onShip: Boolean)

    /**
     * Gets the list of hits taken by the user in the game
     * @param transaction the current transaction
     * @param gameId the game id
     * @param userId the user id
     * @return the list of hits
     */
    fun getHits(transaction: Transaction, gameId: Int, userId: Int): List<Hit>
}
