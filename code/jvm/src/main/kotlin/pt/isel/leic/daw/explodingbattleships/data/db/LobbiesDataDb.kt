package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.LobbiesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Lobby

class LobbiesDataDb : LobbiesData {
    override fun enterLobby(transaction: Transaction, userId: Int, gameType: String): Int =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "insert into lobbies (user_id, game_type, enter_time, game_id) " +
                    "values (:userId, :gameType, now(), null)"
            )
                .bind("userId", userId)
                .bind("gameType", gameType)
                .executeAndReturnGeneratedKeys()
                .mapTo<Int>()
                .first()
        }

    override fun getLobbyById(transaction: Transaction, id: Int): Lobby? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from lobbies where id = :id")
                .bind("id", id)
                .mapTo<Lobby>()
                .firstOrNull()
        }

    override fun searchLobbies(transaction: Transaction, gameType: String, userId: Int): List<Lobby> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery(
                "select * from lobbies where game_type = :gameType " +
                    "and lobbies.user_id <> :userId order by enter_time asc"
            )
                .bind("gameType", gameType)
                .bind("userId", userId)
                .mapTo<Lobby>().list()
        }

    override fun removeLobby(transaction: Transaction, id: Int) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("delete from lobbies where id = :id")
                .bind("id", id)
                .execute()
        }
    }

    override fun setGameId(transaction: Transaction, id: Int, gameId: Int) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("update lobbies set game_id = :gameId where id = :id")
                .bind("gameId", gameId)
                .bind("id", id)
                .execute()
        }
    }
}