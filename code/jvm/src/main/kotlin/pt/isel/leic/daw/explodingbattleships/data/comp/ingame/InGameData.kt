package pt.isel.leic.daw.explodingbattleships.data.comp.ingame

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.HitOutcome
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.Square

interface InGameData {
    fun defineLayout(transaction: Transaction, gameId: Int, playerId: Int, ships: List<Ship>): Boolean

    fun sendHits(transaction: Transaction, gameId: Int, playerId: Int, squares: List<Square>): List<HitOutcome>

    fun playerFleetState(transaction: Transaction) // TODO: add parameters

    fun enemyFleetState(transaction: Transaction) // TODO: add parameters


}
