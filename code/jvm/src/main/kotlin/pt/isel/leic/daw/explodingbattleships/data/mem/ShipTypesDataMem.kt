package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.ShipTypesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.GameType
import pt.isel.leic.daw.explodingbattleships.domain.ShipType

class ShipTypesDataMem(private val mockData: MockData) : ShipTypesData {
    override fun getGameTypeShips(transaction: Transaction, gameType: GameType): List<ShipType> {
        val ships = mutableListOf<ShipType>()
        for (ship in mockData.ship_types) {
            if (ship.gameType == gameType.name) {
                ships.add(ship)
            }
        }
        return ships
    }

    override fun createShipType(transaction: Transaction, shipType: String, size: Int, gameType: String) {
        mockData.ship_types.add(
            ShipType(shipType, size, gameType)
        )
    }
}