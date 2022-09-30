package pt.isel.leic.daw.explodingbattleships.data.comp.ingame

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.TransactionDataDb
import pt.isel.leic.daw.explodingbattleships.domain.HitOutcome
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.domain.getString

class InGameDataDb : InGameData {
    override fun defineLayout(transaction: Transaction, gameId: Int, playerId: Int, ships: List<Ship>): Boolean {
        var success = false
        (transaction as TransactionDataDb).withHandle { handle ->
            ships.forEach { ship ->
                handle.createUpdate("insert into ship values (:square, 0, :orientation, :playerId, :gameId, :type)")
                    .bind("square", ship.square.getString())
                    .bind("orientation", ship.orientation)
                    .bind("playerId", playerId)
                    .bind("gameId", gameId)
                    .bind("type", ship.name?.lowercase())
                    .execute()
            }
            success = true
        }
        return success

    }

    override fun sendHits(transaction: Transaction, gameId: Int, playerId: Int, squares: List<Square>): List<HitOutcome> {
        val hits = mutableListOf<HitOutcome>()
        (transaction as TransactionDataDb).withHandle { handle ->
            squares.forEach { square ->
                handle.createUpdate("insert into hit values (:square, now(), :playerId, :gameId)")
                    .bind("square", square.getString())
                    .bind("playerId", playerId)
                    .bind("gameId", gameId)
                    .execute()
                // check if a ship was hit and if it was, check if it was destroyed
            }
        }
        return hits
    }

    override fun playerFleetState(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override fun enemyFleetState(transaction: Transaction) {
        TODO("Not yet implemented")
    }
}
