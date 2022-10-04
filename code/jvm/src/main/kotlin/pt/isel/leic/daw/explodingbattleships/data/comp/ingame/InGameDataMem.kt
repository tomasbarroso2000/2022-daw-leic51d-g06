package pt.isel.leic.daw.explodingbattleships.data.comp.ingame

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.MockData
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.StoredHit
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.StoredShip
import pt.isel.leic.daw.explodingbattleships.domain.*
import java.sql.Timestamp
import java.time.Instant

class InGameDataMem(private val mockData: MockData) : InGameData {
    override fun defineLayout(
        transaction: Transaction,
        gameId: Int,
        playerId: Int,
        ships: List<VerifiedShip>
    ): Boolean {
        ships.forEach { ship ->
            val storedShip = StoredShip(
                ship.firstSquare.toString(),
                0,
                false,
                ship.orientation,
                playerId,
                gameId,
                ship.name.lowercase()
            )
            mockData.ships.add(storedShip)
        }
        return true
    }

    override fun getShipAndSquares(
        transaction: Transaction,
        gameId: Int,
        playerId: Int
    ): Map<VerifiedShip, Set<VerifiedSquare>> =
        mockData.ships
            .filter { it.game == gameId && it.player == playerId }
            .map { VerifiedShip(it.shipType, it.firstSquare.toVerifiedSquare(), it.orientation) }
            .associateWith { ship -> ship.getSquares() }

    override fun createHit(
        transaction: Transaction,
        square: VerifiedSquare,
        gameId: Int,
        playerId: Int
    ): Int {
        mockData.hits.add(
            StoredHit(
                square.toString(),
                Timestamp.from(Instant.now()),
                playerId,
                gameId
            )
        )
        return 1
    }

    override fun updateNumOfHits(transaction: Transaction, gameId: Int, playerId: Int, shipType: String): Int {
        val storedShip = mockData.ships.find { it.game == gameId && it.player == playerId && it.shipType == shipType }
        mockData.ships.remove(storedShip)
        val newStoredShip = storedShip?.copy(nOfHits = storedShip.nOfHits + 1)
        newStoredShip?.let { mockData.ships.add(it) }
        return 1
    }

    override fun isShipDestroyed(transaction: Transaction, gameId: Int, playerId: Int, shipType: String): Boolean {
        val storedShip = mockData.ships.find { it.game == gameId && it.player == playerId && it.shipType == shipType }
        val size = mockData.shipTypes.find { it.typeName == shipType }?.shipSize
        return storedShip?.nOfHits?.plus(1) == size
    }

    override fun fleetState(transaction: Transaction, gameId: Int, playerId: Int): List<ShipState> =
        mockData
            .ships
            .filter { it.game == gameId && it.player == playerId }
            .map { ShipState(it.shipType, it.destroyed) }
}
