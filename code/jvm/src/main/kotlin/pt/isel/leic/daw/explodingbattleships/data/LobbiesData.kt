package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutput
import pt.isel.leic.daw.explodingbattleships.domain.Lobby
import java.time.Instant

interface LobbiesData {
    fun enterLobby(transaction: Transaction, userId: Int, gameType: String): EnterLobbyOutput

    fun searchLobbies(transaction: Transaction, gameType: String, userId: Int): List<Lobby>

    fun removeLobby(transaction: Transaction, userId: Int, gameType: String, enterTime: Instant)
}
