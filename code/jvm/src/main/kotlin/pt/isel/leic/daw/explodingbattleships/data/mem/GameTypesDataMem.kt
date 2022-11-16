package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.GameTypesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.GameType

class GameTypesDataMem(private val mockData: MockData) : GameTypesData {
    override fun getGameType(transaction: Transaction, gameType: String): GameType? =
        mockData.gameTypes.find { it.name == gameType }

    override fun getGameTypes(transaction: Transaction): List<GameType> =
        mockData.gameTypes

    override fun createGameType(
        transaction: Transaction,
        gameType: String,
        boardSize: Int,
        shotsPerRound: Int,
        layoutDefTime: Int,
        shootingTime: Int
    ) {
        mockData.gameTypes.add(
            GameType(gameType, boardSize, shotsPerRound, layoutDefTime, shootingTime)
        )
    }
}