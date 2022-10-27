package pt.isel.leic.daw.explodingbattleships.domain

import java.time.Instant

/**
 * Represents the hit outcome
 * @property square the square hit
 * @property hitShip if a ship was hit
 * @property destroyedShip the ship destroyed or null if no ship was destroyed
 */
data class HitOutcome(
    val square: Square,
    val hitShip: Boolean,
    val destroyedShip: String? = null
)

/**
 * Represents the outcome of a lot of hits
 * @property hitsOutcome the list of single hits outcome
 * @property win if the user won
 */
data class HitsOutcome(
    val hitsOutcome: List<HitOutcome>,
    val win: Boolean
)

/**
 * Represents a hit
 * @property square the square hit
 * @property hitTimestamp when the hit occurred
 * @property onShip if a ship was hit
 * @property userId the user id
 * @property gameId the game id
 */
data class Hit(
    val square: String,
    val hitTimestamp: Instant,
    val onShip: Boolean,
    val userId: Int,
    val gameId: Int
)