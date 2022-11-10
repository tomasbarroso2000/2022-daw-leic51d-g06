package pt.isel.leic.daw.explodingbattleships.domain

/**
 * Represents a ship name and size
 * @property name the ship name
 * @property size the ship size
 */
data class ShipSpec(val name: String, val size: Int, val gameType: String) {
    init {
        require(name.isNotEmpty())
        require(size > 0)
        require(gameType.isNotEmpty())
    }
}

/**
 * Represents a game type
 * @property boardSize the size of the board
 * @property shotsPerRound the number os shots allowed per round
 * @property layoutDefTimeInSecs the layout definition time limit
 * @property shootingTimeInSecs the time limit ever round has
 */
class GameType(
    val name: String,
    val boardSize: Int,
    val shotsPerRound: Int,
    val layoutDefTimeInSecs: Int,
    val shootingTimeInSecs: Int
)

/**
 *  Gets the ship size or null if there is no ship with the corresponding name
 *  @param shipName the ship name
 *  @param fleetComposition the list of ship specifications depending on the game type
 *  @return the ship size or null
 */
fun getShipSizeOrNull(shipName: String, fleetComposition: List<ShipSpec>): Int? =
    fleetComposition.find { it.name.lowercase() == shipName.lowercase() }?.size