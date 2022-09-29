package pt.isel.leic.daw.explodingbattleships.data.comp.game

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction

interface GameData {
    fun getNumberOfPlayedGames(transaction: Transaction): Int

    fun getGameState(transaction: Transaction, gameId: Int): String?

    fun defineLayout(transaction: Transaction) // TODO: add parameters

    fun sendShots(transaction: Transaction) // TODO: add parameters

    fun playerFleetState(transaction: Transaction) // TODO: add parameters

    fun enemyFleetState(transaction: Transaction) // TODO: add parameters
}
