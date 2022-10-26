package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutput
import pt.isel.leic.daw.explodingbattleships.domain.Lobby
import java.time.Instant

interface LobbiesData {
    /**
     * Adds a user to a lobby or to a game if there is already another user waiting for a game with the same characteristics
     * @param transaction the current transaction
     * @param userId the user id
     * @param gameType the game type the user wants to play
     */
    fun enterLobby(transaction: Transaction, userId: Int, gameType: String)

    /**
     * Searches for lobbies
     */
    fun searchLobbies(transaction: Transaction, gameType: String, userId: Int): List<Lobby>

    fun removeLobby(transaction: Transaction, userId: Int, gameType: String, enterTime: Instant)
}
