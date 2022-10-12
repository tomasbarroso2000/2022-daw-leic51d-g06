package pt.isel.leic.daw.explodingbattleships.domain

data class ShipSpec(val name: String, val size: Int) {
    init {
        require(name.isNotEmpty())
        require(size > 0)
    }
}

enum class GameType(
    val boardSize: Int,
    val shotsPerRound: Int,
    val layoutDefTimeInSecs: Int,
    val shootingTimeInSecs: Int,
    val fleetComposition: List<ShipSpec>
) {
    BEGINNER(
        10, 1, 60, 60, listOf(
            ShipSpec("carrier", 6),
            ShipSpec("battleship", 5),
            ShipSpec("cruiser", 4),
            ShipSpec("submarine", 4),
            ShipSpec("destroyer", 3)
        )
    ),
    EXPERIENCED(
        12, 5, 60, 30, listOf(
            ShipSpec("carrier", 5),
            ShipSpec("battleship", 4),
            ShipSpec("cruiser", 3),
            ShipSpec("submarine", 3),
            ShipSpec("destroyer", 2)
        )
    ),
    EXPERT(
        15, 6, 30, 30, listOf(
            ShipSpec("carrier", 5),
            ShipSpec("battleship", 4),
            ShipSpec("destroyer", 3)
        )
    )
}

fun GameType.getShipSize(shipName: String): Int =
    fleetComposition.find { it.name == shipName }?.size
        ?: throw IllegalArgumentException("No ship found with the name $name")
