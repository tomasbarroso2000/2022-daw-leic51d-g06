package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.ShipTypesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.GameType
import pt.isel.leic.daw.explodingbattleships.domain.ShipType

class ShipTypesDataDb : ShipTypesData {
    override fun getGameTypeShips(transaction: Transaction, gameType: GameType): List<ShipType> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from ship_types where game_type = :gameType")
                .bind("gameType", gameType.name)
                .mapTo<ShipType>().list()
        }

    override fun createShipType(transaction: Transaction, shipType: String, size: Int, gameType: String) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                " insert into ship_types (name, size, game_type) " +
                    "values (:shipType, :size, :gameType)"
            )
                .bind("shipType", shipType)
                .bind("size", size)
                .bind("gameType", gameType)
                .execute()
        }
    }
}