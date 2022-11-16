package pt.isel.leic.daw.explodingbattleships.domain

/**
 * Represents all the information a game has
 * @property game the game
 * @property gameType the type of the game
 * @property opponent the opponent
 * @property playing if the user is the one currently playing
 * @property playerFleet the player's fleet
 * @property takenHits the list of hits that hit a ship
 * @property enemySunkFleet the list of enemy ships sunk
 * @property hits the hits the user sent that hit a ship
 * @property misses the hits the user sent that did not hit a ship
 */
data class FullGameInfo(
    val game: Game,
    val gameType: GameTypeWithFleet,
    val opponent: UserInfo,
    val playing: Boolean,
    val playerFleet: List<Ship>,
    val takenHits: List<Square>,
    val enemySunkFleet: List<Ship>,
    val hits: List<Square>,
    val misses: List<Square>
)