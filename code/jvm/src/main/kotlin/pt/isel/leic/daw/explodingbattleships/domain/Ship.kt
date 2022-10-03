package pt.isel.leic.daw.explodingbattleships.domain

enum class ShipType(val shipName: String, val size: Int) {
    CARRIER("carrier", 5),
    BATTLESHIP("battleship", 4),
    CRUISER("cruiser", 3),
    SUBMARINE("submarine", 3),
    DESTROYER("destroyer", 2)
}

data class ShipFromDb(
    val name: String,
    val firstSquare: String,
    val orientation: String,
)

data class ShipState(
    val destroyed: Boolean,
    val nOfHits: Int
)

fun ShipFromDb.toVerifiedShip() = VerifiedShip(name, firstSquare.toVerifiedSquare(), orientation)

interface Ship {
    val name: String?
    val firstSquare: Square?
    val orientation: String?
}

data class UnverifiedShip(
    override val name: String?,
    override val firstSquare: UnverifiedSquare?,
    override val orientation: String?,
) : Ship

fun UnverifiedShip.toVerifiedShipOrNull(): VerifiedShip? {
    name ?: return null
    val verifiedFirstSquare = firstSquare?.toVerifiedSquareOrNull() ?: return null
    orientation ?: return null
    return VerifiedShip(name, verifiedFirstSquare, orientation)
}

data class VerifiedShip(
    override val name: String,
    override val firstSquare: VerifiedSquare,
    override val orientation: String,
) : Ship

fun VerifiedShip.getSquares(): Set<VerifiedSquare> {
    val squares = mutableSetOf<VerifiedSquare>()
    val size = getSize()
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
        else -> throw throw IllegalArgumentException("Invalid orientation")
    }
    return squares
}

fun Ship.getSize() = ShipType.values().find { it.shipName == name }?.size
    ?: throw IllegalArgumentException("No ship found with the name $name")