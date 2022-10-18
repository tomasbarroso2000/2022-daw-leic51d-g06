package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import org.springframework.stereotype.Component
import pt.isel.leic.daw.explodingbattleships.data.InGameData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.*

class InGameDataDb : InGameData {
    override fun defineLayout(
        transaction: Transaction,
        gameId: Int,
        playerId: Int,
        ships: List<VerifiedShip>
    ): LayoutOutcome =
        (transaction as TransactionDataDb).withHandle { handle ->
            ships.forEach { ship ->
                println(handle.createUpdate("insert into ship values (:firstSquare, :name, :size, 0, false, :orientation, :playerId, :gameId)")
                    .bind("firstSquare", ship.firstSquare.getString())
                    .bind("name", ship.name)
                    .bind("size", ship.size)
                    .bind("orientation", ship.orientation)
                    .bind("playerId", playerId)
                    .bind("gameId", gameId)
                    .execute())
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
                "select name, first_square, orientation, size from ship " +
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
        firstSquare: String
    ): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "update ship set n_of_hits = n_of_hits + 1 " +
                    "where game = :gameId and player = :playerId and first_square = :firstSquare"
            )
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .bind("firstSquare", firstSquare)
                .execute() == 1
        }

    override fun isShipDestroyed(
        transaction: Transaction,
        gameId: Int,
        playerId: Int,
        firstSquare: String
    ): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle
                .createQuery(
                    "select destroyed from ship " +
                        "where game = :gameId and player = :playerId and first_square = :firstSquare"
                )
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .bind("firstSquare", firstSquare)
                .mapTo<Boolean>().first()
        }

    override fun fleetState(transaction: Transaction, gameId: Int, playerId: Int): List<ShipState> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select name, destroyed from ship where game = :gameId and player = :playerId")
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .mapTo<ShipState>().list()
        }

    override fun getNumOfHits(transaction: Transaction, shipFirstSquare: VerifiedSquare, gameId: Int, playerId: Int): Int =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select n_of_hits from ship where first_square = :shipFirstSquare and game = :gameId and player = :playerId")
                .bind("shipFirstSquare", shipFirstSquare.getString())
                .bind("gameId", gameId)
                .bind("playerId", playerId)
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
                .bind("firstSquare", firstSquare.getString())
                .execute() == 1
        }

    override fun hasShips(transaction: Transaction, playerId: Int, gameId: Int): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select exists (select * from ship where game = :gameId and player = :playerId)")
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .mapTo<Boolean>().first()
        }

    override fun setGameStateCompleted(transaction: Transaction, gameId: Int): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("update game set state = 'completed' where id = :gameId")
                .bind("gameId", gameId)
                .execute() == 1
        }
}
