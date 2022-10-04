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

    override fun sendHits(
        transaction: Transaction,
        gameId: Int,
        playerId: Int,
        squares: List<VerifiedSquare>
    ): List<HitOutcome> {
        val hits = mutableListOf<HitOutcome>()
        val shipsSquares =
            mockData.ships
                .filter { it.game == gameId && it.player == playerId }
                .map { VerifiedShip(it.shipType, it.firstSquare.toVerifiedSquare(), it.orientation) }
                .associateWith { ship -> ship.getSquares() }
        squares.forEach {  square ->
            mockData.hits.add(
                StoredHit(
                    square.toString(),
                    Timestamp.from(Instant.now()),
                    playerId,
                    gameId
                )
            )
            val entry = shipsSquares.entries.find { it.value.contains(square) }
            if (entry != null) {
                val ship = entry.key
                val storedShip = mockData.ships.find { it.game == gameId && it.player == playerId && it.shipType == ship.name }
                val size = mockData.shipTypes.find { it.typeName == ship.name }?.shipSize
                mockData.ships.remove(storedShip)
                val newStoredShip =
                    if (storedShip?.nOfHits?.plus(1) == size) {
                        hits.add(HitOutcome(square, true, ship.name))
                        storedShip?.copy(nOfHits = storedShip.nOfHits + 1, destroyed = true)
                    }
                    else {
                        hits.add(HitOutcome(square, true))
                        storedShip?.copy(nOfHits = storedShip.nOfHits + 1)
                    }
                newStoredShip?.let { mockData.ships.add(it) }
            } else {
                hits.add(HitOutcome(square, false))
            }
        }
        return hits
    }

    override fun fleetState(transaction: Transaction, gameId: Int, playerId: Int): List<ShipState> {
        TODO("Not yet implemented")
    }
}
