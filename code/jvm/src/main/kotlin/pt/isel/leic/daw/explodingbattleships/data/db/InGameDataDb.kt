package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.InGameData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.LayoutOutcome
import pt.isel.leic.daw.explodingbattleships.domain.LayoutOutcomeStatus
import pt.isel.leic.daw.explodingbattleships.domain.ShipFromDb
import pt.isel.leic.daw.explodingbattleships.domain.ShipState
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedShip
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare
import pt.isel.leic.daw.explodingbattleships.domain.getSquares
import pt.isel.leic.daw.explodingbattleships.domain.getString
import pt.isel.leic.daw.explodingbattleships.domain.toVerifiedShip

class InGameDataDb : InGameData {
    override fun defineLayout(
        transaction: Transaction,
        gameId: Int,
        playerId: Int,
        ships: List<VerifiedShip>
    ): LayoutOutcome =
        (transaction as TransactionDataDb).withHandle { handle ->
            ships.forEach { ship ->
                handle.createUpdate("insert into ship values (:square, 0, false, :orientation, :playerId, :gameId, :type)")
                    .bind("square", ship.firstSquare.getString())
                    .bind("orientation", ship.orientation)
                    .bind("playerId", playerId)
                    .bind("gameId", gameId)
                    .bind("type", ship.name.lowercase())
                    .execute()
            }
            val isEnemyDone =
                handle.createQuery("select exists (select * from ship where game = :gameId and player <> :playerId)")
                    .bind("gameId", gameId)
                    .bind("playerId", playerId)
                    .mapTo<Boolean>().first()
            if (isEnemyDone) {
                handle.createUpdate("update game set state = 'shooting' where id = :gameId")
                    .bind("gameId", gameId)
                    .execute()
                LayoutOutcome(LayoutOutcomeStatus.STARTED)
            }
            else {
                LayoutOutcome(LayoutOutcomeStatus.WAITING)
            }
        }

    override fun getShipAndSquares(
        transaction: Transaction,
        gameId: Int,
        playerId: Int
    ): Map<VerifiedShip, Set<VerifiedSquare>> =
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
    ): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("insert into hit values (:square, now(), :playerId, :gameId)")
                .bind("square", square.getString())
                .bind("playerId", playerId)
                .bind("gameId", gameId)
                .execute() == 1
        }

    override fun updateNumOfHits(
        transaction: Transaction,
        gameId: Int,
        playerId: Int,
        shipType: String
    ): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "update ship set n_of_hits = n_of_hits + 1 " +
                    "where game = :gameId and player = :playerId and ship_type = :name"
            )
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .bind("name", shipType)
                .execute() == 1
        }

    override fun isShipDestroyed(
        transaction: Transaction,
        gameId: Int,
        playerId: Int,
        shipType: String
    ): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle
                .createQuery(
                    "select destroyed from ship " +
                        "where game = :gameId and player = :playerId and ship_type = :name"
                )
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

    override fun getNumOfHits(transaction: Transaction, shipFirstSquare: VerifiedSquare, gameId: Int, playerId: Int): Int =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select n_of_hits from ship where first_square = :shipFirstSquare and game = :gameId and player = :playerId")
                .bind("shipFirstSquare", shipFirstSquare)
                .bind("gameId", gameId)
                .bind("player", playerId)
                .mapTo<Int>()
                .first()
        }


    /**
     * WIP
     */
    override fun destroyShip(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: VerifiedSquare) : Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "update ship set destroyed = true " +
                        "where game = :gameId and player = :playerId and first_square = :firstSquare"
            )
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .bind("firstSquare", firstSquare)
                .execute() == 1
        }
}
