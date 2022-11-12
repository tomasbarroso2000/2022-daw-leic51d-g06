package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.GameType

interface GameTypesData {
    /**
     * Gets the type of the game
     * @param transaction the current transaction
     * @param gameType the gameType name
     * @return the game state
     */
    fun getGameType(transaction: Transaction, gameType: String): GameType?

    /**
     * Creates a game type
     * @param transaction the current transaction
     * @param gameType the type of game being created
     * @param boardSize
     * @param shotsPerRound
     * @param layoutDefTime
     * @param shootingTime
     */
    fun createGameType(
        transaction: Transaction,
        gameType: String,
        boardSize: Int,
        shotsPerRound: Int,
        layoutDefTime: Int,
        shootingTime: Int
    )
}