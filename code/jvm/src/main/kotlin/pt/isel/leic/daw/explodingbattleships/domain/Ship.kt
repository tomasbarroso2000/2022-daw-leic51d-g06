package pt.isel.leic.daw.explodingbattleships.domain

data class ShipFromDb(
    val name: String,
    val firstSquare: String,
    val orientation: String,
    val size: Int
)

data class ShipState(
    val name: String,
    val destroyed: Boolean
)

fun ShipFromDb.toVerifiedShip() =
    VerifiedShip(name, firstSquare.toVerifiedSquare(), orientation, size)

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

fun UnverifiedShip.toVerifiedShipOrNull(gameType: GameType): VerifiedShip? {
    name ?: return null
    val verifiedFirstSquare = firstSquare?.toVerifiedSquareOrNull() ?: return null
    orientation ?: return null
    return VerifiedShip(name, verifiedFirstSquare, orientation, gameType.getShipSize(name))
}

data class VerifiedShip(
    override val name: String,
    override val firstSquare: VerifiedSquare,
    override val orientation: String,
    val size: Int
) : Ship

fun VerifiedShip.getSquares(): Set<VerifiedSquare> {
    val squares = mutableSetOf<VerifiedSquare>()
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

data class ShipDto(
    val firstSquare: String,
    val name: String,
    val size: Int,
    val nOfHits: Int,
    val destroyed: Boolean,
    val orientation: String,
    val player: Int,
    val game: Int
)


