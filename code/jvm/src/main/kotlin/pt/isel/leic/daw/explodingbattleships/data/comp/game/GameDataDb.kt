package pt.isel.leic.daw.explodingbattleships.data.comp.game


import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.TransactionDataDb
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.domain.getString
import pt.isel.leic.daw.explodingbattleships.domain.toSquare
import java.time.LocalDate

class GameDataDb : GameData {
    override fun getNumberOfPlayedGames(transaction: Transaction): Int {
        var n = 0
        (transaction as TransactionDataDb).withHandle { handle ->
            n = handle.createQuery("select count(*) from game").mapTo<Int>().first()
        }
        return n
    }

    override fun getGameState(transaction: Transaction, gameId: Int): String? {
        var state: String? = null
        (transaction as TransactionDataDb).withHandle { handle ->
            state = handle.createQuery("select state from game where id = :id")
                    .bind("id", gameId)
                    .mapTo<String>()
                    .first()
        }
        return state
    }

    override fun getGame(transaction: Transaction, gameId: Int): Game? {
        var game: Game? = null
        (transaction as TransactionDataDb).withHandle { handle ->
            game = handle.createQuery("select * from game where id = :id")
                .bind("id", gameId)
                .mapTo<Game>().first()
        }
        return game
    }

    override fun defineLayout(transaction: Transaction, gameId: Int, playerId: Int, ships: List<Ship>): Boolean {
        var success = false
        (transaction as TransactionDataDb).withHandle { handle ->
            ships.forEach { ship ->
                handle.createUpdate("insert into ship values (:square, 0, :orientation, :playerId, :gameId, :type)")
                    .bind("square", ship.square.getString())
                    .bind("orientation", ship.orientation)
                    .bind("playerId", playerId)
                    .bind("gameId", gameId)
                    .bind("type", ship.name?.lowercase())
                    .execute()
            }
            success = true
        }
        return success

    }

    override fun sendHits(transaction: Transaction, gameId: Int, playerId: Int, squares: List<Square>): Boolean {
        var success = false
        (transaction as TransactionDataDb).withHandle { handle ->
            squares.forEach { square ->
                handle.createUpdate("insert into hit values (:square, now(), :playerId, :gameId)")
                    .bind("square", square.getString())
                    .bind("playerId", playerId)
                    .bind("gameId", gameId)
                    .execute()
            }
            success = true
        }
        return success
    }

    override fun playerFleetState(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override fun enemyFleetState(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override fun hitSquares(transaction: Transaction, gameId: Int, playerId: Int): List<Square>? {
        var squares: List<Square>? = null
        (transaction as TransactionDataDb).withHandle { handle ->
            squares = handle.createQuery("select square from hit where game = :gameId and player = :playerId")
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .mapTo<String>().list().map { it.toSquare() }
        }
        return squares
    }
}
