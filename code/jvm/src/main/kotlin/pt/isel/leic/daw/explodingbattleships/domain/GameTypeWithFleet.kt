package pt.isel.leic.daw.explodingbattleships.domain

/**
 * Represents a game type
 * @property name the game type name
 * @property boardSize the size of the board
 * @property shotsPerRound the number os shots allowed per round
 * @property layoutDefTimeInSecs the layout definition time limit
 * @property shootingTimeInSecs the time limit ever round has
 * @property fleet the specifications of the fleet
 */
data class GameTypeWithFleet(
    val name: String,
    val boardSize: Int,
    val shotsPerRound: Int,
    val layoutDefTimeInSecs: Int,
    val shootingTimeInSecs: Int,
    val fleet: List<ShipType>
)