package pt.isel.leic.daw.explodingbattleships.data.comp.game

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.MockData

class GameDataMem(private val mockData: MockData) : GameData {
    override fun getNumberOfPlayedGames(transaction: Transaction): Int = mockData.games.size

    override fun getGameState(transaction: Transaction, gameId: Int): String? =
        mockData.games.find { it.id == gameId }?.state

    override fun defineLayout(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override fun sendShots(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override fun playerFleetState(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override fun enemyFleetState(transaction: Transaction) {
        TODO("Not yet implemented")
    }
}
