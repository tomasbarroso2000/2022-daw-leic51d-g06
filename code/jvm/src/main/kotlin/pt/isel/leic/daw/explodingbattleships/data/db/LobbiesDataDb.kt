package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.LobbiesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Lobby
import java.time.Instant

class LobbiesDataDb : LobbiesData {
    override fun enterLobby(transaction: Transaction, userId: Int, gameType: String): Int =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "insert into lobbies (user_id, game_type, enter_time) values (:userId, :gameType, now())"
            )
                .bind("userId", userId)
                .bind("gameType", gameType)
                .executeAndReturnGeneratedKeys()
                .mapTo<Int>()
                .first()
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

    override fun removeLobby(transaction: Transaction, userId: Int, gameType: String, enterTime: Instant) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "delete from lobbies where user_id = :userId and " +
                    "game_type = :gameType and enter_time = :enterTime"
            )
                .bind("userId", userId)
                .bind("gameType", gameType)
                .bind("enterTime", enterTime)
                .execute()
        }
    }
}