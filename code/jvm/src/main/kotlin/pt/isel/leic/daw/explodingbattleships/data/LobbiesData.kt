package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutput
import pt.isel.leic.daw.explodingbattleships.domain.Lobby
import java.time.Instant

interface LobbiesData {
    fun isPlayerInLobby(transaction: Transaction, playerId: Int): Boolean

    fun enterLobby(transaction: Transaction, playerId: Int, gameType: String): EnterLobbyOutput

    fun searchLobbies(transaction: Transaction, gameType: String, playerId: Int): List<Lobby>

    fun removeLobby(transaction: Transaction, playerId: Int, gameType: String, enterTime: Instant)
}