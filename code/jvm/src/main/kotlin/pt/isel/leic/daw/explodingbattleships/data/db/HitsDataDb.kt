package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.HitsData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Hit
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

    override fun getHits(transaction: Transaction, gameId: Int, userId: Int): List<Hit> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from hits where game = :gameId and player = :playerId")
                .bind("gameId", gameId)
                .bind("playerId", userId)
                .mapTo<Hit>().list()
        }

}