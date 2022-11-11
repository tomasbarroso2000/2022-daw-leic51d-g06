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
) {
    /**
     * Get the squares of a ship
     * @return a set with all the ship's squares
     */
    fun getSquares(): Set<Square> {
        val firstSquare = this.firstSquare.toSquare()
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

    /**
     * Converts a Ship into a ShipState
     */
    fun toShipState() = ShipState(name, destroyed)
}