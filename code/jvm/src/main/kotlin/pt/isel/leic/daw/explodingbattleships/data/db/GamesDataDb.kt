package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.GamesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.*
import java.time.Duration
import java.time.Instant

class GamesDataDb : GamesData {
    override fun createGame(
        transaction: Transaction,
        gameType: String,
        player1: Int,
        player2: Int
    ): Int =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                " insert into games (type, state, player1, player2, curr_player, started_at) " +
                        "values (:gameType, 'layout_definition', :player1, :player2, :player1, now())"
            )
                .bind("gameType", gameType)
                .bind("player1", player1)
                .bind("player2", player2)
                .executeAndReturnGeneratedKeys()
                .mapTo<Int>()
                .first()
        }
    override fun getNumberOfPlayedGames(transaction: Transaction) =
        (transaction as TransactionDataDb).withHandle { handle ->
            val nr = handle.createQuery("select count(*) from games").mapTo<Int>().first()
            NumberOfPlayedGames(nr)
        }

    override fun getGameState(transaction: Transaction, gameId: Int) =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select state from games where id = :id")
                .bind("id", gameId)
                .mapTo<String>().firstOrNull()?.let { GameState(it) }
        }

    override fun getGame(transaction: Transaction, gameId: Int): Game? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from games where id = :id")
                .bind("id", gameId)
                .mapTo<Game>().firstOrNull()
        }

    override fun getHitSquares(transaction: Transaction, gameId: Int, playerId: Int): List<VerifiedSquare> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select square from hits where game = :gameId and player = :playerId")
                .bind("gameId", gameId)
                .bind("playerId", playerId)
                .mapTo<String>().list().map { it.toVerifiedSquare() }
        }

    override fun getPlayerGame(transaction: Transaction, playerId: Int): Game? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from games where (player1 = :playerId or player2 = :playerId) and state <> 'completed'")
                .bind("playerId", playerId)
                .mapTo<Game>().firstOrNull()
        }

    override fun changeCurrPlayer(transaction: Transaction, gameId: Int, newCurrPlayer: Int) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("update games set curr_player = :newCurrPlayer, started_at = now() where id = :gameId")
                .bind("newCurrPlayer", newCurrPlayer)
                .bind("gameId", gameId)
                .execute()
        }
    }

    override fun setGameToShooting(transaction: Transaction, gameId: Int) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("update games set state = 'shooting', started_at = now() where id = :gameId")
                .bind("gameId", gameId)
                .execute()
        }
    }



    override fun setGameStateCompleted(transaction: Transaction, gameId: Int) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("update games set state = 'completed' where id = :gameId")
                .bind("gameId", gameId)
                .execute()
        }
    }
}
