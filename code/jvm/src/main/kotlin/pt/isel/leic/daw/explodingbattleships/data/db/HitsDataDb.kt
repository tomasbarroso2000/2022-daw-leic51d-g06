package pt.isel.leic.daw.explodingbattleships.data.db

import pt.isel.leic.daw.explodingbattleships.data.HitsData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare
import pt.isel.leic.daw.explodingbattleships.domain.getString

class HitsDataDb: HitsData {
    override fun createHit(
        transaction: Transaction,
        square: VerifiedSquare,
        gameId: Int,
        playerId: Int,
        onShip: Boolean
    ) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("insert into hits values (:square, now(), :onShip, :playerId, :gameId)")
                .bind("square", square.getString())
                .bind("onShip", onShip)
                .bind("playerId", playerId)
                .bind("gameId", gameId)
                .execute()
        }
    }

}