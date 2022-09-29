package pt.isel.leic.daw.explodingbattleships.data.comp.game


import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.TransactionDataDb
import pt.isel.leic.daw.explodingbattleships.domain.Game

class GameDataDb : GameData {
    override fun getNumberOfPlayedGames(transaction: Transaction): Int {
        var n = 0
        (transaction as TransactionDataDb).withHandle { handle ->
            n = handle.createQuery("select count(*) from game").mapTo<Int>().first()
        }
        return n
    }

    override fun getGameState(transaction: Transaction, gameId: Int): String? {
        var state: String? = null
        (transaction as TransactionDataDb).withHandle { handle ->
            state = handle.createQuery("select state from game where id = :id")
                    .bind("id", gameId)
                    .mapTo<String>()
                    .first()
        }
        return state
    }

    override fun getGame(transaction: Transaction, gameId: Int): Game? {
        TODO("Not yet implemented")
    }

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
