package pt.isel.leic.daw.explodingbattleships.domain

/**
 * Represents a ship
 * @property firstSquare the ship's first square
 * @property name the ship's name
 * @property size the ship's size
 * @property nOfHits the number of hits the ship has taken
 * @property destroyed if the ship is destroyed
 * @property orientation the ship's orientation
 * @property userId the user's id
 * @property gameId the game's id
 */
data class Ship(
    val firstSquare: String,
    val name: String,
    val size: Int,
    val nOfHits: Int,
    val destroyed: Boolean,
    val orientation: String,
    val userId: Int,
    val gameId: Int
)

/**
 * Represents the ship state
 * @property name the ship's name
 * @property destroyed if the ship was destroyed
 */
data class ShipState(
    val name: String,
    val destroyed: Boolean
)

/**
 * Converts a Ship into a ShipState
 */
fun Ship.toShipState() = ShipState(name, destroyed)

/**
 * Represents the information needed to create a ship
 * @property name the ship's name
 * @property firstSquare the ship's first square
 * @property orientation the ship's orientation
 */
data class ShipCreationInfo(
    val name: String,
    val firstSquare: Square,
    val orientation: String
)

/**
 * Converts a ShipCreationInfo to a Ship or null
 * @param userId the user's id
 * @param gameId the game's id
 * @param fleetComposition the list of ships of the corresponding game type
 * @return the ship or null
 */
fun ShipCreationInfo.toShipOrNull(userId: Int, gameId: Int, fleetComposition: List<ShipType>): Ship? {
    val shipSize = getShipSizeOrNull(name, fleetComposition) ?: return null
    return Ship(firstSquare.getString(), name, shipSize, 0, false, orientation, userId, gameId)
}

/**
 * Get the squares of a ship
 * @return a set with all the ship's squares
 */
fun Ship.getSquares(): Set<Square> {
    val firstSquare = this.firstSquare.toSquareOrNull() ?: throw IllegalArgumentException("Invalid square")
    val squares = mutableSetOf<Square>()
    var currentSquare = firstSquare
    when (orientation.lowercase()) {
        "vertical" -> for (i in 0 until size) {
            squares.add(currentSquare)
            currentSquare = currentSquare.down()
        }
        "horizontal" -> for (i in 0 until size) {
            squares.add(currentSquare)
            currentSquare = currentSquare.right()
        }
        else -> throw IllegalArgumentException("Invalid orientation")
    }
    return squares
}