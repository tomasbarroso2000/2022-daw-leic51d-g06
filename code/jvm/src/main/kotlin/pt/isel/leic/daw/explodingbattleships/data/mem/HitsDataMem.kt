package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.HitsData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Hit
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare
import java.sql.Timestamp
import java.time.Instant

class HitsDataMem(private val mockData: MockData) : HitsData {
    override fun createHit(
        transaction: Transaction,
        square: VerifiedSquare,
        gameId: Int,
        playerId: Int,
        onShip: Boolean
    ) {
        mockData.hits.add(
            StoredHit(
                square.toString(),
                Timestamp.from(Instant.now()),
                onShip,
                playerId,
                gameId
            )
        )
    }

    override fun getHits(transaction: Transaction, gameId: Int, userId: Int): List<Hit> =
        mockData
            .hits
            .filter { it.game == gameId && it.player == userId }
            .map { it.toHit() }
}