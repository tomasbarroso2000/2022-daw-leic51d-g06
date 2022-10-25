package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.LobbiesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutput
import pt.isel.leic.daw.explodingbattleships.domain.Lobby
import java.time.Instant

class LobbiesDataMem(private val mockData: MockData) : LobbiesData {
    override fun enterLobby(transaction: Transaction, playerId: Int, gameType: String): EnterLobbyOutput {
        mockData.lobby.add(Lobby(playerId, gameType, Instant.now()))
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