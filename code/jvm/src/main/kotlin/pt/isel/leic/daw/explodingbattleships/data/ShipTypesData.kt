package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.GameType
import pt.isel.leic.daw.explodingbattleships.domain.ShipType

interface ShipTypesData {
    /**
     * Gets all the specifications of the ships from the corresponding game type
     * @param transaction the current transction
     * @param gameType the game type
     * @return all the ships
     */
    fun getGameTypeShips(transaction: Transaction, gameType: GameType): List<ShipType>

    /**
     * Creates a ship type
     * @param transaction the current transaction
     * @param shipType the type of ship being created
     * @param size
     * @param gameType
     */
    fun createShipType(transaction: Transaction, shipType: String, size: Int, gameType: String)
}