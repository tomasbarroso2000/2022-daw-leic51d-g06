package pt.isel.leic.daw.explodingbattleships.data.comp.player

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.MockData
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.StoredPlayer
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.StoredToken
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutput
import pt.isel.leic.daw.explodingbattleships.domain.PlayerOutput
import pt.isel.leic.daw.explodingbattleships.domain.TokenOutput
import java.util.UUID

class PlayerDataMem(private val mockData: MockData) : PlayerData {
    override fun getPlayerIdByToken(transaction: Transaction, token: String): Int? =
        mockData.tokens.find { equals(token) }?.player

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

    override fun enterLobby(transaction: Transaction, playerId: Int): EnterLobbyOutput? {
        TODO("Not yet implemented")
    }
}
