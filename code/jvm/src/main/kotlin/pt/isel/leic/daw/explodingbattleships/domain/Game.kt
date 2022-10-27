package pt.isel.leic.daw.explodingbattleships.domain

import java.time.Instant

/**
 * Represents a game
 * @property id the game id
 * @property type the game type
 * @property state the game state
 * @property player1 one of the users
 * @property player2 the other user
 * @property currPlayer the user currently playing
 * @property startedAt the instant the current turn started at
 */
data class Game(
    val id: Int,
    val type: String,
    val state: String,
    val player1: Int,
    val player2: Int,
    val currPlayer: Int,
    val startedAt: Instant
)

/**
 * Gets the user not currently playing
 */
fun Game.idlePlayer() = if (currPlayer == player1) player2 else player1

/**
 * Gets the player in the game not corresponding to the id passed as a parameter
 * @param playerId the user id
 */
fun Game.otherPlayer(playerId: Int) =
    if (player1 == playerId) {
        player2
    } else if (player2 == playerId) {
        player1
    } else {
        throw IllegalArgumentException("Player not in game")
    }

/**
 * Represents all the information a game has
 * @property game the game
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
    val opponent: Int,
    val playing: Boolean,
    val playerFleet: List<Ship>,
    val takenHits: List<Square>,
    val enemySunkFleet: List<Ship>,
    val hits: List<Square>,
    val misses: List<Square>
)
