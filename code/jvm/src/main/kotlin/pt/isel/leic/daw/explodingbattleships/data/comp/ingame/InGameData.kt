package pt.isel.leic.daw.explodingbattleships.data.comp.ingame

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.HitOutcome
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedShip
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare

interface InGameData {
    fun defineLayout(transaction: Transaction, gameId: Int, playerId: Int, ships: List<VerifiedShip>): Boolean

    fun sendHits(transaction: Transaction, gameId: Int, playerId: Int, squares: List<VerifiedSquare>): List<HitOutcome>

    fun playerFleetState(transaction: Transaction) // TODO: add parameters

    fun enemyFleetState(transaction: Transaction) // TODO: add parameters


}
