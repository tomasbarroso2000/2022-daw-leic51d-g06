package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.LobbiesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Lobby
import java.time.Instant

class LobbiesDataMem(private val mockData: MockData) : LobbiesData {

    override fun enterLobby(transaction: Transaction, userId: Int, gameType: String): Int {
        val id = mockData.lobbies.maxOf { it.id } + 1
        mockData.lobbies.add(Lobby(id, userId, gameType, Instant.now(), null))
        return id
    }

    override fun getLobbyById(transaction: Transaction, id: Int): Lobby? =
        mockData.lobbies.find { it.id == id }

    override fun searchLobbies(transaction: Transaction, gameType: String, userId: Int): List<Lobby> =
        mockData.lobbies
            .filter { it.gameType == gameType && it.userId != userId && it.gameId == null }
            .sortedBy { it.enterTime }

    override fun removeLobby(transaction: Transaction, id: Int) {
        mockData.lobbies.removeIf { it.id == id }
    }

    override fun setGameId(transaction: Transaction, id: Int, gameId: Int) {
        mockData.lobbies.find { it.id == id }?.let {
            val newLobby = it.copy(gameId = gameId)
            mockData.lobbies.remove(it)
            mockData.lobbies.add(newLobby)
        }
    }
}