package pt.isel.leic.daw.explodingbattleships.data.comp.games

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.TransactionDataDb
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare
import pt.isel.leic.daw.explodingbattleships.domain.toVerifiedSquare

class GamesDataDb : GamesData {
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

    override fun getHitSquares(transaction: Transaction, gameId: Int, playerId: Int): List<VerifiedSquare>? {
        var squares: List<VerifiedSquare>? = null
        (transaction as TransactionDataDb).withHandle { handle ->
            squares = handle.createQuery("select square from hit where game = :gameId and player = :playerId")
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .mapTo<String>().list().map { it.toVerifiedSquare() }
        }
        return squares
    }
}