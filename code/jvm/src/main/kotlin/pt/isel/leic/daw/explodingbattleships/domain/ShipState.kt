package pt.isel.leic.daw.explodingbattleships.domain

/**
 * Represents the ship state
 * @property name the ship's name
 * @property destroyed if the ship was destroyed
 */
data class ShipState(
    val name: String,
    val destroyed: Boolean
)