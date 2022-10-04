package pt.isel.leic.daw.explodingbattleships.data.comp.players

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.MockData
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.StoredPlayer
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.StoredToken
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.getSublist
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.hasMore
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.toPlayer
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutput
import pt.isel.leic.daw.explodingbattleships.domain.ListOfData
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.domain.PlayerOutput
import pt.isel.leic.daw.explodingbattleships.domain.TokenOutput
import java.util.UUID

class PlayersDataMem(private val mockData: MockData) : PlayersData {
    override fun getPlayerIdFromToken(transaction: Transaction, token: String): Int? =
        mockData.tokens.find { it.tokenVer == token }?.player

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

    override fun getRankings(transaction: Transaction, limit: Int, skip: Int): ListOfData<Player> {
        val players = mockData.players.map { it.toPlayer() }.sortedBy { it.score }.reversed()
        return ListOfData(getSublist(players, limit, skip), hasMore(players.size, limit, skip))
    }

    override fun enterLobby(
        transaction: Transaction,
        playerId: Int,
        width: Int,
        height: Int,
        hitsPerRound: Int
    ): EnterLobbyOutput {
        TODO("Not yet implemented")
    }
}
