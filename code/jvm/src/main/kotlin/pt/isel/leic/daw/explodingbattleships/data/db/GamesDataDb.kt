package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.GamesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.GameType
import pt.isel.leic.daw.explodingbattleships.domain.ShipSpec

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

    override fun getGameType(transaction: Transaction, game: Game): GameType? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery(
                "select name, board_size, shots_per_round, layout_def_time_in_secs, shooting_time_in_secs " +
                    "from game_types join games on game_types.name = games.type where games.id = :id"
            )
                .bind("id", game.id)
                .mapTo<GameType>().first()
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

    override fun getAllGameTypesNames(transaction: Transaction): List<String> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select name from game_types")
                .mapTo<String>().list()
        }

    override fun getGameTypeShips(transaction: Transaction, gameType: GameType): List<ShipSpec> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from ship_types where game_type = :gameType")
                .bind("gameType", gameType)
                .mapTo<ShipSpec>().list()
        }
}