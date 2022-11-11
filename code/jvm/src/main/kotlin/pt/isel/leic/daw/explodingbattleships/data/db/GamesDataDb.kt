package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.GamesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.GameType
import pt.isel.leic.daw.explodingbattleships.domain.ShipType

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
    override fun getNumberOfPlayedGames(transaction: Transaction): Int =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select count(*) from games").mapTo<Int>().first()
        }

    override fun getGameState(transaction: Transaction, gameId: Int): String? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select state from games where id = :id")
                .bind("id", gameId)
                .mapTo<String>().firstOrNull()
        }

    override fun getGameType(transaction: Transaction, gameType: String): GameType? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from game_types where name = :gameType")
                .bind("gameType", gameType)
                .mapTo<GameType>().firstOrNull()
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

    override fun getGameTypeShips(transaction: Transaction, gameType: GameType): List<ShipType> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from ship_types where game_type = :gameType")
                .bind("gameType", gameType.name)
                .mapTo<ShipType>().list()
        }
}