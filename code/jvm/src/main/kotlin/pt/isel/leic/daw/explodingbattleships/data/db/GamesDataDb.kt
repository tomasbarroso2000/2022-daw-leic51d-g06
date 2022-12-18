package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.GamesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.DataList
import pt.isel.leic.daw.explodingbattleships.domain.Game

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

    override fun getOngoingGames(transaction: Transaction, userId: Int, limit: Int, skip: Int): DataList<Game> =
        (transaction as TransactionDataDb).withHandle { handle ->
            val foundGames = handle.createQuery(
                "select * from games " +
                    "where (player1 = :userId or player2 = :userId) and state <> 'completed' " +
                    "order by id offset :skip limit :limit"
            )
                .bind("userId", userId)
                .bind("skip", skip)
                .bind("limit", limit + 1)
                .mapTo<Game>().list()
            val games = mutableListOf<Game>()
            val hasMore = getHasMoreAndProcessList(foundGames, games, limit)
            DataList(games, hasMore)
        }

    override fun getNumberOfPlayedGames(transaction: Transaction): Int =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select count(*) from games").mapTo<Int>().first()
        }

    override fun getGame(transaction: Transaction, gameId: Int): Game? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from games where id = :id")
                .bind("id", gameId)
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