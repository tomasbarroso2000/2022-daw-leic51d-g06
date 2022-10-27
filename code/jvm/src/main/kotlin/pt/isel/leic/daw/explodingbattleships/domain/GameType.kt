package pt.isel.leic.daw.explodingbattleships.domain

/**
 * Represents a ship name and size
 * @property name the ship name
 * @property size the ship size
 */
data class ShipSpec(val name: String, val size: Int) {
    init {
        require(name.isNotEmpty())
        require(size > 0)
    }
}

/**
 * Represents a game type
 * @property boardSize the size of the board
 * @property shotsPerRound the number os shots allowed per round
 * @property layoutDefTimeInSecs the layout definition time limit
 * @property shootingTimeInSecs the time limit ever round has
 * @property fleetComposition the list of ship allowed
 */
enum class GameType(
    val boardSize: Int,
    val shotsPerRound: Int,
    val layoutDefTimeInSecs: Int,
    val shootingTimeInSecs: Int,
    val fleetComposition: List<ShipSpec>
) {
    BEGINNER(
        10,
        1,
        60,
        60,
        listOf(
            ShipSpec("carrier", 6),
            ShipSpec("battleship", 5),
            ShipSpec("cruiser", 4),
            ShipSpec("submarine", 4),
            ShipSpec("destroyer", 3)
        )
    ),
    EXPERIENCED(
        12,
        5,
        60,
        30,
        listOf(
            ShipSpec("carrier", 5),
            ShipSpec("battleship", 4),
            ShipSpec("cruiser", 3),
            ShipSpec("submarine", 3),
            ShipSpec("destroyer", 2)
        )
    ),
    EXPERT(
        15,
        6,
        30,
        30,
        listOf(
            ShipSpec("carrier", 5),
            ShipSpec("battleship", 4),
            ShipSpec("destroyer", 3)
        )
    )
}

/**
 *  Gets the ship size or null if there is no ship with the corresponding name
 *  @param shipName the ship name
 *  @return the ship size or null
 */
fun GameType.getShipSizeOrNull(shipName: String): Int? =
    fleetComposition.find { it.name.lowercase() == shipName.lowercase() }?.size
