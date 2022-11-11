package pt.isel.leic.daw.explodingbattleships.domain

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