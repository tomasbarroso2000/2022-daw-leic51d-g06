package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.ShipsData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.domain.getSquares

class ShipsDataMem(private val mockData: MockData) : ShipsData {
    override fun defineLayout(
        transaction: Transaction,
        gameId: Int,
        userId: Int,
        ships: List<Ship>
    ) {
        mockData.ships.addAll(ships)
    }

    override fun checkEnemyLayoutDone(transaction: Transaction, gameId: Int, userId: Int): Boolean =
        mockData.ships.any { it.gameId == gameId && it.userId != userId }

    override fun getShipsAndSquares(
        transaction: Transaction,
        gameId: Int,
        userId: Int
    ): Map<Ship, Set<Square>> =
        mockData.ships
            .filter { it.gameId == gameId && it.userId == userId }
            .associateWith { ship -> ship.getSquares() }

    override fun updateNumOfHits(transaction: Transaction, gameId: Int, userId: Int, firstSquare: String) {
        val storedShip = mockData
            .ships
            .find { it.gameId == gameId && it.userId == userId && it.firstSquare == firstSquare }
        mockData.ships.remove(storedShip)
        val size = mockData.ships.find { it.firstSquare == firstSquare }?.size
        val destroyed = storedShip?.nOfHits?.plus(1) == size
        val newStoredShip = storedShip?.copy(nOfHits = storedShip.nOfHits + 1, destroyed = destroyed)
        newStoredShip?.let { mockData.ships.add(it) }
    }

    override fun getFleet(transaction: Transaction, gameId: Int, userId: Int): List<Ship> =
        mockData
            .ships
            .filter { it.gameId == gameId && it.userId == userId }

    override fun getShip(
        transaction: Transaction,
        firstSquare: String,
        gameId: Int,
        userId: Int
    ): Ship? =
        mockData.ships.find { it.gameId == gameId && it.userId == userId && it.firstSquare == firstSquare }

    override fun destroyShip(
        transaction: Transaction,
        gameId: Int,
        userId: Int,
        firstSquare: String
    ) {
        mockData.ships
            .find { it.gameId == gameId && it.userId == userId && it.firstSquare == firstSquare }
            ?.let { ship ->
                mockData.ships.remove(ship)
                val newShip = ship.copy(destroyed = true)
                mockData.ships.add(newShip)
            }
    }

    override fun hasShips(transaction: Transaction, userId: Int, gameId: Int) =
        mockData.ships.any { it.gameId == gameId && it.userId == userId }
}