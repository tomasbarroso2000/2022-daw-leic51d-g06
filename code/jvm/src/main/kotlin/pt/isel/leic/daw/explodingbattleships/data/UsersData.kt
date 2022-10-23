package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.*

interface UsersData {
    fun getUserFromToken(transaction: Transaction, token: String): User?

    fun createUser(transaction: Transaction, name: String, email: String, password: Int): UserOutput

    fun createToken(transaction: Transaction, userId: Int): TokenOutput

    fun getRankings(transaction: Transaction, limit: Int, skip: Int): Rankings
}
