package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.LobbiesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Lobby
import java.time.Instant

class LobbiesDataMem(private val mockData: MockData) : LobbiesData {
    override fun enterLobby(transaction: Transaction, userId: Int, gameType: String) {
        mockData.lobbies.add(Lobby(userId, gameType, Instant.now()))
    }

    override fun searchLobbies(transaction: Transaction, gameType: String, userId: Int): List<Lobby> {
        val sameTypeLobbies = mutableListOf<Lobby>()
        mockData.lobbies
            .filter { it.gameType == gameType && it.userId != userId }
            .sortedBy { it.enterTime }
            .forEach { sameTypeLobbies.add(Lobby(it.userId, it.gameType, it.enterTime)) }
        return sameTypeLobbies
    }

    override fun removeLobby(transaction: Transaction, userId: Int, gameType: String, enterTime: Instant) {
        mockData.lobbies.find { it.gameType == gameType && it.userId == userId }?.let {
            mockData.lobbies.remove(it)
        }
    }
}
