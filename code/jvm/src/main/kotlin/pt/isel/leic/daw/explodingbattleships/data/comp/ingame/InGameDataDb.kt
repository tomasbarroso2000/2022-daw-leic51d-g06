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
    ): Boolean =
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
            true
        }

    override fun getShipAndSquares(
        transaction: Transaction,
        gameId: Int,
        playerId: Int
    ) : Map<VerifiedShip, Set<VerifiedSquare>> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery(
                "select ship_type.type_name name, first_square, orientation from ship join ship_type on ship.ship_type = ship_type.type_name " +
                        "where game = :gameId and player = :playerId"
            )
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .mapTo<ShipFromDb>().list().map { it.toVerifiedShip() }.associateWith { ship -> ship.getSquares() }
        }

    override fun createHit(
        transaction: Transaction,
        square: VerifiedSquare,
        gameId: Int,
        playerId: Int
    ): Int =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("insert into hit values (:square, now(), :playerId, :gameId)")
                .bind("square", square.getString())
                .bind("playerId", playerId)
                .bind("gameId", gameId)
                .execute()
        }

    override fun updateNumOfHits(
        transaction: Transaction,
        gameId: Int,
        playerId: Int,
        shipType: String
    ): Int =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "update ship set n_of_hits = n_of_hits + 1 " +
                        "where game = :gameId and player = :playerId and ship_type = :name"
            )
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .bind("name", shipType)
                .execute()
        }

    override fun isShipDestroyed(
        transaction: Transaction,
        gameId: Int,
        playerId: Int,
        shipType: String
    ): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle
                .createQuery("select destroyed from ship " +
                        "where game = :gameId and player = :playerId and ship_type = :name")
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .bind("name", shipType)
                .mapTo<Boolean>().first()
        }

    override fun fleetState(transaction: Transaction, gameId: Int, playerId: Int): List<ShipState> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select ship_type, destroyed from ship where game = :gameId and player = :playerId")
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .mapTo<ShipState>().list()
        }
}
