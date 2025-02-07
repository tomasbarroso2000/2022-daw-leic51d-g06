package pt.isel.leic.daw.explodingbattleships.domain

/**
 * Represents a game type
 * @property name the game type name
 * @property boardSize the size of the board
 * @property shotsPerRound the number os shots allowed per round
 * @property layoutDefTimeInSecs the layout definition time limit
 * @property shootingTimeInSecs the time limit ever round has
 */
data class GameType(
    val name: String,
    val boardSize: Int,
    val shotsPerRound: Int,
    val layoutDefTimeInSecs: Int,
    val shootingTimeInSecs: Int
)