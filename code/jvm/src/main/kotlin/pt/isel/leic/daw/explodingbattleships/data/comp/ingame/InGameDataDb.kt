package pt.isel.leic.daw.explodingbattleships.data.comp.ingame

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.TransactionDataDb
import pt.isel.leic.daw.explodingbattleships.domain.*

class InGameDataDb : InGameData {
    override fun defineLayout(
        transaction: Transaction,
        gameId: Int,
        playerId: Int,
        ships: List<VerifiedShip>
    ): Boolean {
        var success = false
        (transaction as TransactionDataDb).withHandle { handle ->
            ships.forEach { ship ->
                handle.createUpdate("insert into ship values (:square, 0, false, :orientation, :playerId, :gameId, :type)")
                    .bind("square", ship.firstSquare.getString())
                    .bind("orientation", ship.orientation)
                    .bind("playerId", playerId)
                    .bind("gameId", gameId)
                    .bind("type", ship.name.lowercase())
                    .execute()
                // check if the game can go into shooting phase
            }
            success = true
        }
        return success

    }

    override fun sendHits(
        transaction: Transaction,
        gameId: Int,
        playerId: Int,
        squares: List<VerifiedSquare>
    ): List<HitOutcome> {
        val hits = mutableListOf<HitOutcome>()
        (transaction as TransactionDataDb).withHandle { handle ->
            val shipsSquares =
                handle.createQuery(
                    "select ship_type.type_name name, first_square, orientation from ship join ship_type on ship.ship_type = ship_type.type_name " +
                            "where game = :gameId and player = :playerId"
                )
                    .bind("gameId", gameId)
                    .bind("playerId", playerId)
                    .mapTo<ShipFromDb>().list().map { it.toVerifiedShip() }.associateWith { ship -> ship.getSquares() }
            squares.forEach { square ->
                handle.createUpdate("insert into hit values (:square, now(), :playerId, :gameId)")
                    .bind("square", square.getString())
                    .bind("playerId", playerId)
                    .bind("gameId", gameId)
                    .execute()
                val entry = shipsSquares.entries.find { it.value.contains(square) }
                if (entry != null) {
                    handle.createUpdate("update ship set n_of_hits = n_of_hits + 1 " +
                            "where game = :gameId and player = :playerId and ship_type = :name")
                        .bind("gameId", gameId)
                        .bind("playerId", playerId)
                        .bind("name", entry.key.name)
                        .execute()
                    val destroyed = handle
                        .createQuery("select destroyed from ship " +
                                "where game = :gameId and player = :playerId and ship_type = :name")
                        .bind("gameId", gameId)
                        .bind("playerId", playerId)
                        .bind("name", entry.key.name)
                        .mapTo<Boolean>().first()
                    if (destroyed)
                        hits.add(HitOutcome(square, true, entry.key.name))
                    else
                        hits.add(HitOutcome(square, true))
                } else {
                    hits.add(HitOutcome(square, false))
                }
            }
        }
        return hits
    }

    override fun playerFleetState(transaction: Transaction, gameId: Int, playerId: Int): List<ShipState> {
        val ships = mutableListOf<ShipState>()
        (transaction as TransactionDataDb).withHandle { handle ->
            val ships: List<ShipState> =
                handle.createQuery(
                    "select destroyed, n_of_hits from ship where game = :gameId and player = :playerId"
                )
                    .bind("gameId", gameId)
                    .bind("playerId", playerId)
                    .mapTo<ShipState>().list()
        }
        return ships
    }
}
