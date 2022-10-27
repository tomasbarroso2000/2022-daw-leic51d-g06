package pt.isel.leic.daw.explodingbattleships.data

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
     * @param transaction the current transaction
     * @param gameType the game type
     * @param userId the user id
     * @return a list with the corresponding lobbies
     */
    fun searchLobbies(transaction: Transaction, gameType: String, userId: Int): List<Lobby>

    /**
     * Removes a lobby
     * @param transaction the current transaction
     * @param userId the user id
     * @param gameType the game type
     * @param enterTime the instant the player entered the lobby
     */
    fun removeLobby(transaction: Transaction, userId: Int, gameType: String, enterTime: Instant)
}