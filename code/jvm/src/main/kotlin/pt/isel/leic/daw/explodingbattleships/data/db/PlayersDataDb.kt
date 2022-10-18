package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.PlayersData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.*
import java.time.Instant
import java.util.UUID

class PlayersDataDb : PlayersData {
    override fun getPlayerFromToken(transaction: Transaction, token: String): Player? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.select("select id, name, score from token join player on player = id where token_ver = :token")
                .bind("token", token)
                .mapTo<Player>().firstOrNull()
        }

    override fun createPlayer(transaction: Transaction, name: String, email: String, password: Int): PlayerOutput =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("insert into player (name, email, score, password_ver) values (:name, :email, 0, :password)")
                .bind("name", name)
                .bind("email", email)
                .bind("password", password)
                .executeAndReturnGeneratedKeys()
                .mapTo<Int>()
                .first().let { PlayerOutput(it) }
        }

    override fun createToken(transaction: Transaction, playerId: Int): TokenOutput =
        (transaction as TransactionDataDb).withHandle { handle ->
            val token = UUID.randomUUID().toString()
            handle.createUpdate("insert into token values (:token, :playerId)")
                .bind("token", token)
                .bind("playerId", playerId)
                .execute()
            TokenOutput(token)
        }

    override fun getRankings(transaction: Transaction, limit: Int, skip: Int): Rankings =
        (transaction as TransactionDataDb).withHandle { handle ->
            val foundPlayers =
                handle.createQuery("select id, name, score from player order by score desc offset :skip limit :limit")
                    .bind("skip", skip)
                    .bind("limit", limit + 1)
                    .mapTo<Player>().list()
            val players = mutableListOf<Player>()
            val hasMore = getHasMoreAndProcessList(foundPlayers, players, limit)
            Rankings(ListOfData(players, hasMore))
        }

    override fun isPlayerInLobby(transaction: Transaction, playerId: Int): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select exists (select * from lobby where player = :playerId)")
                .bind("playerId", playerId)
                .mapTo<Boolean>().first()
        }

    override fun enterLobby(transaction: Transaction, playerId: Int, gameType: String): EnterLobbyOutput =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("insert into lobby values (:playerId, :gameType, now())")
                .bind("playerId", playerId)
                .bind("gameType", gameType)
                .execute()
            EnterLobbyOutput(true, null)
        }

    override fun searchLobbies(transaction: Transaction, gameType: String): List<Lobby> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from lobby where game_type = :gameType order by enter_time asc")
                .bind("gameType", gameType)
                .mapTo<Lobby>().list()
        }

    override fun removeLobby(transaction: Transaction, playerId: Int, gameType: String, enterTime: Instant) =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("delete from lobby where player = :playerId game_type = :gameType and enter_time = :enterTime")
                .bind("playerId", playerId)
                .bind("gameType", gameType)
                .bind("enterTime", enterTime)
                .execute() == 1
        }
}
