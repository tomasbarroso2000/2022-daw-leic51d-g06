package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.HitsData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Hit
import pt.isel.leic.daw.explodingbattleships.domain.Square

class HitsDataDb : HitsData {
    override fun createHit(
        transaction: Transaction,
        square: Square,
        gameId: Int,
        userId: Int,
        onShip: Boolean
    ) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("insert into hits values (:square, now(), :onShip, :userId, :gameId)")
                .bind("square", square.toString())
                .bind("onShip", onShip)
                .bind("userId", userId)
                .bind("gameId", gameId)
                .execute()
        }
    }

    override fun getHits(transaction: Transaction, gameId: Int, userId: Int): List<Hit> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from hits where game_id = :gameId and user_id = :userId")
                .bind("gameId", gameId)
                .bind("userId", userId)
                .mapTo<Hit>().list()
        }
}