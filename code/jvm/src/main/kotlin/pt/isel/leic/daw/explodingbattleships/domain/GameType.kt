package pt.isel.leic.daw.explodingbattleships.domain

/**
 * Represents a game type
 * @property boardSize the size of the board
 * @property shotsPerRound the number os shots allowed per round
 * @property layoutDefTimeInSecs the layout definition time limit
 * @property shootingTimeInSecs the time limit ever round has
 */
class GameType(
    val name: String,
    val boardSize: Int,
    val shotsPerRound: Int,
    val layoutDefTimeInSecs: Int,
    val shootingTimeInSecs: Int
)