package pt.isel.leic.daw.explodingbattleships.domain

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
) {
    /**
     * Converts a ShipCreationInfo to a Ship or null
     * @param userId the user's id
     * @param gameId the game's id
     * @param fleetComposition the list of ships of the corresponding game type
     * @return the ship or null
     */
    fun toShipOrNull(userId: Int, gameId: Int, fleetComposition: List<ShipType>): Ship? {
        val shipSize = fleetComposition.find { it.name == name }?.size ?: return null
        return Ship(firstSquare.toString(), name, shipSize, 0, false, orientation, userId, gameId)
    }
}