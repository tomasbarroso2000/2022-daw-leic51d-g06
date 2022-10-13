package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.InGameData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.*
import java.sql.Timestamp
import java.time.Instant
class InGameDataMem(private val mockData: MockData) : InGameData {
    override fun defineLayout(
        transaction: Transaction,
        gameId: Int,
        playerId: Int,
        ships: List<VerifiedShip>
    ): LayoutOutcome {
        ships.forEach { ship ->
            val storedShip = StoredShip(
                ship.firstSquare.toString(),
                ship.name.lowercase(),
                ship.size, //ERROR
                0,
                false,
                ship.orientation,
                playerId,
                gameId
            )
            mockData.ships.add(storedShip)
        }

        return if (mockData.ships.any { it.game == gameId && it.player != playerId }) {
            mockData.games.find { it.id == gameId }?.let { game ->
                mockData.games.remove(game)
                val newGame = game.copy(state = "shooting")
                mockData.games.add(newGame)
            }
            LayoutOutcome(LayoutOutcomeStatus.STARTED)
        }
        else {
            LayoutOutcome(LayoutOutcomeStatus.WAITING)
        }
    }

    override fun getShipAndSquares(
        transaction: Transaction,
        gameId: Int,
        playerId: Int
    ): Map<VerifiedShip, Set<VerifiedSquare>> =
        mockData.ships
            .filter { it.game == gameId && it.player == playerId }
            .map { it.toVerifiedShip() }
            .associateWith { ship -> ship.getSquares() }

    override fun createHit(
        transaction: Transaction,
        square: VerifiedSquare,
        gameId: Int,
        playerId: Int
    ): Boolean =
        mockData.hits.add(
            StoredHit(
                square.toString(),
                Timestamp.from(Instant.now()),
                playerId,
                gameId
            )
        )

    override fun updateNumOfHits(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: String): Boolean {
        val storedShip = mockData.ships.find { it.game == gameId && it.player == playerId && it.firstSquare == firstSquare }
        mockData.ships.remove(storedShip)
        val size = mockData.ships.find { it.firstSquare == firstSquare }?.size
        val destroyed = storedShip?.nOfHits?.plus(1) == size
        val newStoredShip = storedShip?.copy(nOfHits = storedShip.nOfHits + 1, destroyed = destroyed)
        newStoredShip?.let { return mockData.ships.add(it) }
        return false
    }

    override fun isShipDestroyed(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: String): Boolean =
        mockData.ships.find { it.game == gameId && it.player == playerId && it.firstSquare == firstSquare }?.destroyed ?: false

    override fun fleetState(transaction: Transaction, gameId: Int, playerId: Int): List<ShipState> =
        mockData
            .ships
            .filter { it.game == gameId && it.player == playerId }
            .map { it.toShipState() }

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
    ): Boolean {
        mockData.ships
            .find { it.game == gameId && it.player == playerId && it.firstSquare.toVerifiedSquare() == firstSquare }
            ?.let { ship ->
                mockData.ships.remove(ship)
                val newShip = ship.copy(destroyed = true)
                return mockData.ships.add(newShip)
            }
        return false
    }

    override fun hasShips(transaction: Transaction, playerId: Int, gameId: Int) =
        mockData.ships.any { it.game == gameId && it.player == playerId }

    override fun setGameStateCompleted(transaction: Transaction, gameId: Int): Boolean {
        val storedGame = mockData.games.find { it.id == gameId }
        if (storedGame != null) {
            mockData.games.remove(storedGame)
            mockData.games.add(storedGame.copy(state = "completed"))
            return true
        }
        return false
    }
}

