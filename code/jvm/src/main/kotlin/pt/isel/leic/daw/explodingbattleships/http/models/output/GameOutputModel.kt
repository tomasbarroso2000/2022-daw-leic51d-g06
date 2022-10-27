package pt.isel.leic.daw.explodingbattleships.http.models.output

import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.Square
import java.time.Instant

/**
 * Represents the information needed to show the user the game
 * @param id the game id
 * @param type the game type
 * @param state the game state
 * @param opponent the player's opponent
 * @param playing if the player is currently playing
 * @param startedAt the instant the current turn started at
 * @param fleet the list of ships
 * @param takenHits the list of square hit
 * @param enemySunkFleet the list of enemy ships destroyed
 * @param hits the list of enemy squares where a ship was hit
 * @param misses the list of enemy squares hit without hitting a ship
 */
data class GameOutputModel(
    val id: Int,
    val type: String,
    val state: String,
    val opponent: Int,
    val playing: Boolean,
    val startedAt: Instant,
    val fleet: List<Ship>,
    val takenHits: List<Square>,
    val enemySunkFleet: List<Ship>,
    val hits: List<Square>,
    val misses: List<Square>
)