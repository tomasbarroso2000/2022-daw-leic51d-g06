package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.data.UsersData
import pt.isel.leic.daw.explodingbattleships.domain.DataList
import pt.isel.leic.daw.explodingbattleships.domain.Ranking
import pt.isel.leic.daw.explodingbattleships.domain.User
import java.util.UUID

class UsersDataMem(private val mockData: MockData) : UsersData {
    override fun getUserFromToken(transaction: Transaction, token: String): User? {
        val userId =
            mockData
                .tokens
                .find { it.tokenVer == token }?.userId
        return mockData
            .users
            .find { player -> player.id == userId }
    }

    override fun getUserFromEmail(transaction: Transaction, email: String): User? =
        mockData.users.find { it.email == email }

    override fun createUser(transaction: Transaction, name: String, email: String, password: Int): Int {
        if (mockData.users.any { it.email == email })
            throw DataException("Email $email is already in use")
        val id = mockData.users.maxOf { it.id } + 1
        mockData.users.add(User(id, name, email, 0, password))
        return id
    }

    override fun createToken(transaction: Transaction, userId: Int): String {
        val token = UUID.randomUUID().toString()
        mockData.tokens.add(StoredToken(token, userId))
        return token
    }

    override fun getRankings(transaction: Transaction, limit: Int, skip: Int): DataList<Ranking> {
        val rankings = mockData.users.map { it.toRanking() }.sortedBy { it.score }.reversed()
        return DataList(getSublist(rankings, limit, skip), hasMore(rankings.size, limit, skip))
    }
}
