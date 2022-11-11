package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.ShipsData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.Square

class ShipsDataDb : ShipsData {
    override fun defineLayout(transaction: Transaction, gameId: Int, userId: Int, ships: List<Ship>) =
        (transaction as TransactionDataDb).withHandle { handle ->
            ships.forEach { ship ->
                handle.createUpdate(
                    "insert into ships values (:firstSquare, :name, " +
                        ":size, 0, false, :orientation, :userId, :gameId)"
                )
                    .bind("firstSquare", ship.firstSquare)
                    .bind("name", ship.name)
                    .bind("size", ship.size)
                    .bind("orientation", ship.orientation)
                    .bind("userId", userId)
                    .bind("gameId", gameId)
                    .execute()
            }
        }

    override fun checkEnemyLayoutDone(transaction: Transaction, gameId: Int, userId: Int): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select exists (select * from ships where game_id = :gameId and user_id <> :userId)")
                .bind("gameId", gameId)
                .bind("userId", userId)
                .mapTo<Boolean>()
                .first()
        }

    override fun getShipsAndSquares(
        transaction: Transaction,
        gameId: Int,
        userId: Int
    ): Map<Ship, Set<Square>> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from ships where game_id = :gameId and user_id = :userId")
                .bind("gameId", gameId)
                .bind("userId", userId)
                .mapTo<Ship>().list().associateWith { ship -> ship.getSquares() }
        }

    override fun updateNumOfHits(transaction: Transaction, gameId: Int, userId: Int, firstSquare: String) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "update ships set n_of_hits = n_of_hits + 1 " +
                    "where game_id = :gameId and user_id = :userId and first_square = :firstSquare"
            )
                .bind("gameId", gameId)
                .bind("userId", userId)
                .bind("firstSquare", firstSquare)
                .execute()
        }
    }

    override fun getFleet(transaction: Transaction, gameId: Int, userId: Int): List<Ship> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from ships where game_id = :gameId and user_id = :userId")
                .bind("gameId", gameId)
                .bind("userId", userId)
                .mapTo<Ship>().list()
        }

    override fun getShip(
        transaction: Transaction,
        firstSquare: String,
        gameId: Int,
        userId: Int
    ): Ship? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery(
                "select * from ships where " +
                    "first_square = :firstSquare and game_id = :gameId and user_id = :userId"
            )
                .bind("firstSquare", firstSquare)
                .bind("gameId", gameId)
                .bind("userId", userId)
                .mapTo<Ship>()
                .firstOrNull()
        }

    override fun destroyShip(transaction: Transaction, gameId: Int, userId: Int, firstSquare: String): Unit =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "update ships set destroyed = true " +
                    "where game_id = :gameId and user_id = :userId and first_square = :firstSquare"
            )
                .bind("gameId", gameId)
                .bind("userId", userId)
                .bind("firstSquare", firstSquare)
                .execute()
        }

    override fun hasShips(transaction: Transaction, userId: Int, gameId: Int): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select exists (select * from ships where game_id = :gameId and user_id = :userId)")
                .bind("gameId", gameId)
                .bind("userId", userId)
                .mapTo<Boolean>().first()
        }
}