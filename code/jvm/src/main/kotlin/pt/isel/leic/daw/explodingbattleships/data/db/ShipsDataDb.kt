package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.ShipsData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.*

class ShipsDataDb: ShipsData {
    override fun defineLayout(transaction: Transaction, gameId: Int, playerId: Int, ships: List<VerifiedShip>) =
        (transaction as TransactionDataDb).withHandle { handle ->
            ships.forEach { ship ->
                handle.createUpdate("insert into ships values (:firstSquare, :name, :size, 0, false, :orientation, :playerId, :gameId)")
                    .bind("firstSquare", ship.firstSquare.getString())
                    .bind("name", ship.name)
                    .bind("size", ship.size)
                    .bind("orientation", ship.orientation)
                    .bind("playerId", playerId)
                    .bind("gameId", gameId)
                    .execute()
            }
        }

    override fun checkEnemyLayoutDone(transaction: Transaction, gameId: Int, playerId: Int): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select exists (select * from ships where game = :gameId and player <> :playerId)")
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .mapTo<Boolean>()
                .first()
        }

    override fun getShipsAndSquares(
        transaction: Transaction,
        gameId: Int,
        playerId: Int
    ): Map<VerifiedShip, Set<VerifiedSquare>> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery(
                "select name, first_square, orientation, size from ships " +
                        "where game = :gameId and player = :playerId"
            )
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .mapTo<ShipFromDb>().list().map { it.toVerifiedShip() }.associateWith { ship -> ship.getSquares() }
        }

    override fun updateNumOfHits(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: String) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "update ships set n_of_hits = n_of_hits + 1 " +
                        "where game = :gameId and player = :playerId and first_square = :firstSquare"
            )
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .bind("firstSquare", firstSquare)
                .execute()
        }
    }

    override fun isShipDestroyed(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: String): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle
                .createQuery(
                    "select destroyed from ships " +
                            "where game = :gameId and player = :playerId and first_square = :firstSquare"
                )
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .bind("firstSquare", firstSquare)
                .mapTo<Boolean>().first()
        }

    override fun fleetState(transaction: Transaction, gameId: Int, playerId: Int): List<ShipState> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select name, destroyed from ships where game = :gameId and player = :playerId")
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .mapTo<ShipState>().list()
        }

    override fun getFleet(transaction: Transaction, gameId: Int, playerId: Int): List<ShipDto> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from ships where game = :gameId and player = :playerId")
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .mapTo<ShipDto>().list()
        }

    override fun getNumOfHits(
        transaction: Transaction,
        shipFirstSquare: VerifiedSquare,
        gameId: Int,
        playerId: Int
    ): Int =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select n_of_hits from ships where first_square = :shipFirstSquare and game = :gameId and player = :playerId")
                .bind("shipFirstSquare", shipFirstSquare.getString())
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .mapTo<Int>()
                .first()
        }

    override fun destroyShip(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: VerifiedSquare): Unit =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "update ships set destroyed = true " +
                        "where game = :gameId and player = :playerId and first_square = :firstSquare"
            )
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .bind("firstSquare", firstSquare.getString())
                .execute()
        }

    override fun hasShips(transaction: Transaction, playerId: Int, gameId: Int): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select exists (select * from ships where game = :gameId and player = :playerId)")
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .mapTo<Boolean>().first()
        }
}