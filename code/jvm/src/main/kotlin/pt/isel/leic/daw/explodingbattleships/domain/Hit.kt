package pt.isel.leic.daw.explodingbattleships.domain

import java.time.Instant

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