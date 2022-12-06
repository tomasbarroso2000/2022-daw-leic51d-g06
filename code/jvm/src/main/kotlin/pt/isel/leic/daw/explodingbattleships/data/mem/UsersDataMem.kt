package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.data.UsersData
import pt.isel.leic.daw.explodingbattleships.domain.DataList
import pt.isel.leic.daw.explodingbattleships.domain.User
import pt.isel.leic.daw.explodingbattleships.domain.UserInfo

class UsersDataMem(private val mockData: MockData) : UsersData {

    override fun getUserById(transaction: Transaction, userId: Int): User? =
        mockData.users.find { it.id == userId }

    override fun getUserFromEmail(transaction: Transaction, email: String): User? =
        mockData.users.find { it.email == email }

    override fun createUser(transaction: Transaction, name: String, email: String, passwordVer: String): Int {
        if (mockData.users.any { it.email == email }) {
            throw DataException("Already in use", "Email $email is already in use")
        }
        val id = mockData.users.maxOf { it.id } + 1
        mockData.users.add(User(id, name, email, 0, passwordVer))
        return id
    }

    override fun getRankings(transaction: Transaction, limit: Int, skip: Int): DataList<UserInfo> {
        val rankings = mockData.users.map { it.toRanking() }.sortedBy { it.score }.reversed()
        return DataList(getSublist(rankings, limit, skip), hasMore(rankings.size, limit, skip))
    }

    override fun increasePlayerScore(transaction: Transaction, userId: Int, pointsReceived: Int) {
        val storedUser = mockData.users.find { it.id == userId }
        if (storedUser != null) {
            mockData.users.remove(storedUser)
            mockData.users.add(storedUser.copy(score = storedUser.score + pointsReceived))
        }
    }
}