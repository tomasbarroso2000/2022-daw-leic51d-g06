package pt.isel.leic.daw.explodingbattleships.data.comp.ingame

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.MockData
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.HitOutcome
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.Square

class InGameDataMem(private val mockData: MockData) : InGameData {
    override fun defineLayout(transaction: Transaction, gameId: Int, playerId: Int, ships: List<Ship>): Boolean {
        TODO("Not yet implemented")
    }

    override fun sendHits(transaction: Transaction, gameId: Int, playerId: Int, squares: List<Square>): List<HitOutcome> {
        TODO("Not yet implemented")
    }

    override fun playerFleetState(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override fun enemyFleetState(transaction: Transaction) {
        TODO("Not yet implemented")
    }
}
