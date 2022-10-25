package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.ShipsData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.*

class ShipsDataMem(private val mockData: MockData) : ShipsData {
    override fun defineLayout(
        transaction: Transaction,
        gameId: Int,
        playerId: Int,
        ships: List<VerifiedShip>
    ) {
        mockData.ships.addAll(
            ships.map { ship ->
                StoredShip(
                    ship.firstSquare.toString(),
                    ship.name.lowercase(),
                    ship.size,
                    0,
                    false,
                    ship.orientation,
                    playerId,
                    gameId
                )
            }
        )
    }

    override fun checkEnemyLayoutDone(transaction: Transaction, gameId: Int, playerId: Int): Boolean =
        mockData.ships.any { it.game == gameId && it.player != playerId }

    override fun getShipsAndSquares(
        transaction: Transaction,
        gameId: Int,
        playerId: Int
    ): Map<VerifiedShip, Set<VerifiedSquare>> =
        mockData.ships
            .filter { it.game == gameId && it.player == playerId }
            .map { it.toVerifiedShip() }
            .associateWith { ship -> ship.getSquares() }

    override fun updateNumOfHits(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: String) {
        val storedShip = mockData.ships.find { it.game == gameId && it.player == playerId && it.firstSquare == firstSquare }
        mockData.ships.remove(storedShip)
        val size = mockData.ships.find { it.firstSquare == firstSquare }?.size
        val destroyed = storedShip?.nOfHits?.plus(1) == size
        val newStoredShip = storedShip?.copy(nOfHits = storedShip.nOfHits + 1, destroyed = destroyed)
        newStoredShip?.let { mockData.ships.add(it) }
    }

    override fun isShipDestroyed(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: String): Boolean =
        mockData.ships.find { it.game == gameId && it.player == playerId && it.firstSquare == firstSquare }?.destroyed ?: false


    override fun fleetState(transaction: Transaction, gameId: Int, playerId: Int): List<ShipState> =
        mockData
            .ships
            .filter { it.game == gameId && it.player == playerId }
            .map { it.toShipState() }

    override fun getFleet(transaction: Transaction, gameId: Int, playerId: Int): List<ShipDto> =
        mockData
            .ships
            .filter { it.game == gameId && it.player == playerId }
            .map { it.toShip() }

    override fun getNumOfHits(
        transaction: Transaction,
        shipFirstSquare: VerifiedSquare,
        gameId: Int,
        playerId: Int
    ): Int {
        mockData.ships.find { it.game == gameId && it.player == playerId && it.firstSquare.toVerifiedSquare() == shipFirstSquare }?. let {
            return it.nOfHits
        }
        return -1
    }

    override fun destroyShip(
        transaction: Transaction,
        gameId: Int,
        playerId: Int,
        firstSquare: VerifiedSquare
    ) {
        mockData.ships
            .find { it.game == gameId && it.player == playerId && it.firstSquare.toVerifiedSquare() == firstSquare }
            ?.let { ship ->
                mockData.ships.remove(ship)
                val newShip = ship.copy(destroyed = true)
                mockData.ships.add(newShip)
            }
    }

    override fun hasShips(transaction: Transaction, playerId: Int, gameId: Int) =
        mockData.ships.any { it.game == gameId && it.player == playerId }
}