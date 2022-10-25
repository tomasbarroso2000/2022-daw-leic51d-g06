package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.HitsData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Hit
import pt.isel.leic.daw.explodingbattleships.domain.Square
import java.time.Instant

class HitsDataMem(private val mockData: MockData) : HitsData {
    override fun createHit(
        transaction: Transaction,
        square: Square,
        gameId: Int,
        playerId: Int,
        onShip: Boolean
    ) {
        mockData.hits.add(
            Hit(
                square.toString(),
                Instant.now(),
                onShip,
                playerId,
                gameId
            )
        )
    }

    override fun getHits(transaction: Transaction, gameId: Int, userId: Int): List<Hit> =
        mockData.hits.filter { it.game == gameId && it.player == userId }
}