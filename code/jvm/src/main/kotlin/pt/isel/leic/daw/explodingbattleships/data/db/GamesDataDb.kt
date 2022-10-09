package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.GamesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare
import pt.isel.leic.daw.explodingbattleships.domain.toVerifiedSquare

class GamesDataDb : GamesData {
    override fun getNumberOfPlayedGames(transaction: Transaction): Int =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select count(*) from game").mapTo<Int>().first()
        }

    override fun getGameState(transaction: Transaction, gameId: Int): String? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select state from game where id = :id")
                .bind("id", gameId)
                .mapTo<String>().firstOrNull()
        }

    override fun getGame(transaction: Transaction, gameId: Int): Game? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from game where id = :id")
                .bind("id", gameId)
                .mapTo<Game>().firstOrNull()
        }

    override fun getHitSquares(transaction: Transaction, gameId: Int, playerId: Int): List<VerifiedSquare> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select square from hit where game = :gameId and player = :playerId")
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .mapTo<String>().list().map { it.toVerifiedSquare() }
        }

    override fun getPlayerGame(transaction: Transaction, playerId: Int): Game? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from game where (player1 = :playerId or player2 = :playerId) and state <> 'completed'")
                .bind("playerId", playerId)
                .mapTo<Game>().firstOrNull()
        }

    override fun changeCurrPlayer(transaction: Transaction, gameId: Int, newCurrPlayer: Int): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("update game set curr_player = :newCurrPlayer where id = :gameId")
                .bind("newCurrPlayer", newCurrPlayer)
                .bind("gameId", gameId)
                .execute() == 1
        }
}
