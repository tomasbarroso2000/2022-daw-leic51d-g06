package pt.isel.leic.daw.explodingbattleships.domain

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

data class ShipState(
    val name: String,
    val destroyed: Boolean
)

fun Ship.toShipState() = ShipState(name, destroyed)

data class ShipCreationInfo(
    val name: String,
    val firstSquare: Square,
    val orientation: String
)

fun ShipCreationInfo.toShipOrNull(userId: Int, gameId: Int, gameType: GameType): Ship? {
    val shipSize = gameType.getShipSizeOrNull(name) ?: return null
    return Ship(firstSquare.getString(), name, shipSize, 0, false, orientation, userId, gameId)
}

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
