package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.PlayersData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.getHasMoreAndProcessList
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutput
import pt.isel.leic.daw.explodingbattleships.domain.ListOfData
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.domain.PlayerOutput
import pt.isel.leic.daw.explodingbattleships.domain.TokenOutput
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

    override fun getRankings(transaction: Transaction, limit: Int, skip: Int): ListOfData<Player> =
        (transaction as TransactionDataDb).withHandle { handle ->
            val foundPlayers =
                handle.createQuery("select id, name, score from player order by score desc offset :skip limit :limit")
                    .bind("skip", skip)
                    .bind("limit", limit + 1)
                    .mapTo<Player>().list()
            val players = mutableListOf<Player>()
            val hasMore = getHasMoreAndProcessList(foundPlayers, players, limit)
            ListOfData(players, hasMore)
        }

    override fun isPlayerInLobby(transaction: Transaction, playerId: Int): Boolean =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select exists (select * from lobby where player = :playerId)")
                .bind("playerId", playerId)
                .mapTo<Boolean>().first()
        }

    override fun enterLobby(transaction: Transaction, playerId: Int, width: Int, height: Int, hitsPerRound: Int): EnterLobbyOutput =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("insert into lobby values (:playerId, :width, :height, :hitsPerRound)")
                .bind("playerId", playerId)
                .bind("width", width)
                .bind("height", height)
                .bind("hitsPerRound", hitsPerRound)
                .execute()
            EnterLobbyOutput(true)
        }
}
