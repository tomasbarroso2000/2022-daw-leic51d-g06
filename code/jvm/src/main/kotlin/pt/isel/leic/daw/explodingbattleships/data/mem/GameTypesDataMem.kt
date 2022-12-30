package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.GameTypesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.GameType
import pt.isel.leic.daw.explodingbattleships.domain.GameTypeWithFleet
import pt.isel.leic.daw.explodingbattleships.domain.ShipType

class GameTypesDataMem(private val mockData: MockData) : GameTypesData {
    override fun getGameType(transaction: Transaction, gameTypeName: String): GameTypeWithFleet? {
        val gameType = mockData.gameTypes.find { it.name == gameTypeName } ?: return null
        val fleet = mockData.shipTypes.filter { it.gameType == gameType.name }
        return GameTypeWithFleet(
            gameType.name,
            gameType.boardSize,
            gameType.shotsPerRound,
            gameType.layoutDefTimeInSecs,
            gameType.shootingTimeInSecs,
            fleet
        )
    }

    override fun getGameTypes(transaction: Transaction): List<GameTypeWithFleet> =
        mockData.gameTypes.map { gameType ->
            GameTypeWithFleet(
                gameType.name,
                gameType.boardSize,
                gameType.shotsPerRound,
                gameType.layoutDefTimeInSecs,
                gameType.shootingTimeInSecs,
                mockData.shipTypes.filter { it.gameType == gameType.name }
            )
        }

    override fun createGameType(
        transaction: Transaction,
        gameType: String,
        boardSize: Int,
        shotsPerRound: Int,
        layoutDefTime: Int,
        shootingTime: Int,
        fleet: List<ShipType>
    ) {
        mockData.gameTypes.add(
            GameType(gameType, boardSize, shotsPerRound, layoutDefTime, shootingTime)
        )
        mockData.shipTypes.addAll(fleet)
    }
}