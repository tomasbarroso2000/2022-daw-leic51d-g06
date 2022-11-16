package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.GameTypeWithFleet
import pt.isel.leic.daw.explodingbattleships.domain.ShipType

interface GameTypesData {
    /**
     * Gets the type of the game
     * @param transaction the current transaction
     * @param gameTypeName the gameType name
     * @return the game state
     */
    fun getGameType(transaction: Transaction, gameTypeName: String): GameTypeWithFleet?

    /**
     * Gets all the game types available
     * @param transaction the current transaction
     * @return the game types
     */
    fun getGameTypes(transaction: Transaction): List<GameTypeWithFleet>

    /**
     * Creates a game type
     * @param transaction the current transaction
     * @param gameType the type of game being created
     * @param boardSize the size of the board
     * @param shotsPerRound the number of shots per round
     * @param layoutDefTime the layout definition time
     * @param shootingTime the shooting time
     * @param fleet the fleet for the game type
     */
    fun createGameType(
        transaction: Transaction,
        gameType: String,
        boardSize: Int,
        shotsPerRound: Int,
        layoutDefTime: Int,
        shootingTime: Int,
        fleet: List<ShipType>
    )
}