package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.UsersData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.*
import java.util.UUID

class UsersDataMem(private val mockData: MockData) : UsersData {
    override fun getUserFromToken(transaction: Transaction, token: String): User? {
        val playerId =
            mockData
                .tokens
                .find { it.tokenVer == token }?.player
        return mockData
            .players
            .find { player -> player.id == playerId }
            ?.let { User(it.id, it.name, it.score) }
    }

    override fun createUser(transaction: Transaction, name: String, email: String, password: Int): UserOutput {
        val id = mockData.players.maxOf { it.id } + 1
        mockData.players.add(StoredPlayer(id, name, email, 0, password))
        return UserOutput(id)
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
}
