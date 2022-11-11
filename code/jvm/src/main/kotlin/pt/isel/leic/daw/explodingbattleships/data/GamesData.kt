package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.GameType
import pt.isel.leic.daw.explodingbattleships.domain.ShipType

interface GamesData {
    /**
     * Creates a game
     * @param transaction the current transaction
     * @param gameType the type of the game being created
     * @param player1
     * @param player2
     * @return the game id
     */
    fun createGame(transaction: Transaction, gameType: String, player1: Int, player2: Int): Int

    /**
     * Gets the number of played games so far
     * @param transaction the current transaction
     * @return the number of played games
     */
    fun getNumberOfPlayedGames(transaction: Transaction): Int

    /**
     * Gets the type of the game
     * @param transaction the current transaction
     * @param gameType the gameType name
     * @return the game state
     */
    fun getGameType(transaction: Transaction, gameType: String): GameType?

    /**
     * Gets the game with corresponding game id
     * @param transaction the current transaction
     * @param gameId the game id
     * @return the corresponding game
     */
    fun getGame(transaction: Transaction, gameId: Int): Game?

    /**
     * Changes the player currently playing
     * @param transaction the current transaction
     * @param gameId the game id
     * @param newCurrPlayer the new current playing player
     */
    fun changeCurrPlayer(transaction: Transaction, gameId: Int, newCurrPlayer: Int)

    /**
     * Changes the game state to shooting
     * @param transaction the current transaction
     * @param gameId the game to be changed
     */
    fun setGameToShooting(transaction: Transaction, gameId: Int)

    /**
     * Sets the game state to completed
     * @param transaction the current transaction
     * @param gameId the game to be changed
     */
    fun setGameStateCompleted(transaction: Transaction, gameId: Int)

    /**
     * Gets all the specifications of the ships from the corresponding game type
     * @param transaction the current transction
     * @param gameType the game type
     * @return all the ships
     */
    fun getGameTypeShips(transaction: Transaction, gameType: GameType): List<ShipType>
}