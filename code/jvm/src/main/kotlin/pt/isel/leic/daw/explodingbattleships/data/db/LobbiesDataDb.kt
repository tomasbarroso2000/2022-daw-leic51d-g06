package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.LobbiesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutput
import pt.isel.leic.daw.explodingbattleships.domain.Lobby
import java.time.Instant

class LobbiesDataDb: LobbiesData {
    override fun enterLobby(transaction: Transaction, playerId: Int, gameType: String): EnterLobbyOutput =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("insert into lobbies values (:playerId, :gameType, now())")
                .bind("playerId", playerId)
                .bind("gameType", gameType)
                .execute()
            EnterLobbyOutput(true, null)
        }

    override fun searchLobbies(transaction: Transaction, gameType: String, playerId: Int): List<Lobby> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from lobbies where game_type = :gameType and player <> :playerId order by enter_time asc")
                .bind("gameType", gameType)
                .bind("playerId", playerId)
                .mapTo<Lobby>().list()
        }

    override fun removeLobby(transaction: Transaction, playerId: Int, gameType: String, enterTime: Instant) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("delete from lobbies where player = :playerId and game_type = :gameType and enter_time = :enterTime")
                .bind("playerId", playerId)
                .bind("gameType", gameType)
                .bind("enterTime", enterTime)
                .execute()
        }
    }
}