package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.*
import java.time.Instant

interface PlayersData {
    fun getPlayerFromToken(transaction: Transaction, token: String): PlayerOutputModel?

    fun createPlayer(transaction: Transaction, name: String, email: String, password: Int): PlayerOutput

    fun createToken(transaction: Transaction, playerId: Int): TokenOutput

    fun getRankings(transaction: Transaction, limit: Int, skip: Int): Rankings

    fun isPlayerInLobby(transaction: Transaction, playerId: Int): Boolean

    fun enterLobby(transaction: Transaction, playerId: Int, gameType: String): EnterLobbyOutput

    fun searchLobbies(transaction: Transaction, gameType: String): List<Lobby>

    fun removeLobby(transaction: Transaction, playerId: Int, gameType: String, enterTime: Instant): Boolean
}
