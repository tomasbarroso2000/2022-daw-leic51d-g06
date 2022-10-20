package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.PlayersData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.*
import java.time.Instant
import java.util.UUID

class PlayersDataMem(private val mockData: MockData) : PlayersData {
    override fun getPlayerFromToken(transaction: Transaction, token: String): Player? {
        val playerId =
            mockData
                .tokens
                .find { it.tokenVer == token }?.player
        return mockData
            .players
            .find { player -> player.id == playerId }
            ?.let { Player(it.id, it.name, it.score) }
    }

    override fun createPlayer(transaction: Transaction, name: String, email: String, password: Int): PlayerOutput {
        val id = mockData.players.maxOf { it.id } + 1
        mockData.players.add(StoredPlayer(id, name, email, 0, password))
        return PlayerOutput(id)
    }

    override fun createToken(transaction: Transaction, playerId: Int): TokenOutput {
        val token = UUID.randomUUID().toString()
        mockData.tokens.add(StoredToken(token, playerId))
        return TokenOutput(token)
    }

    override fun getRankings(transaction: Transaction, limit: Int, skip: Int): Rankings {
        val players = mockData.players.map { it.toPlayer() }.sortedBy { it.score }.reversed()
        return Rankings(ListOfData(getSublist(players, limit, skip), hasMore(players.size, limit, skip)))
    }

    override fun isPlayerInLobby(transaction: Transaction, playerId: Int): Boolean =
        mockData.lobby.any { it.player == playerId }

    override fun enterLobby(transaction: Transaction, playerId: Int, gameType: String): EnterLobbyOutput {
        mockData.lobby.add(StoredLobby(playerId, gameType, Instant.now()))
        return EnterLobbyOutput(true, null)
    }

    override fun searchLobbies(transaction: Transaction, gameType: String, playerId: Int): List<Lobby> {
        val sameTypeLobbies = mutableListOf<Lobby>()
        mockData.lobby
            .filter { it.gameType == gameType && it.player != playerId}
            .sortedBy { it.enterTime }
            .forEach { sameTypeLobbies.add(Lobby(it.player, it.gameType, it.enterTime)) }

        return sameTypeLobbies
    }


    override fun removeLobby(transaction: Transaction, playerId: Int, gameType: String, enterTime: Instant) {
        mockData.lobby.find { it.gameType == gameType && it.player == playerId }?.let {
            mockData.lobby.remove(it)
        }
    }
}
