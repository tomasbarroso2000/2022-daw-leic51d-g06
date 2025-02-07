package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.Lobby

interface LobbiesData {
    /**
     * Adds a user to a lobby or to a game if there is already another user waiting for a game with the same characteristics
     * @param transaction the current transaction
     * @param userId the user id
     * @param gameType the game type the user wants to play
     * @return the lobby id
     */
    fun enterLobby(transaction: Transaction, userId: Int, gameType: String): Int

    /**
     * Gets a lobby by its id
     * @param transaction the current transaction
     * @param id the lobby id
     * @return the found lobby
     */
    fun getLobbyById(transaction: Transaction, id: Int): Lobby?

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
     * @param id the lobby id
     */
    fun removeLobby(transaction: Transaction, id: Int)

    /**
     * Sets a game id for a lobby
     * @param transaction the current transaction
     * @param id the lobby id
     * @param gameId the game id
     */
    fun setGameId(transaction: Transaction, id: Int, gameId: Int)
}