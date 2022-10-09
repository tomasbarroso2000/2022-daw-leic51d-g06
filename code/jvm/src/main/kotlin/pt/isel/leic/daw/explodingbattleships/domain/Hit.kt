package pt.isel.leic.daw.explodingbattleships.domain

data class Hits(val squares: List<UnverifiedSquare>?)

data class HitOutcome(
    val square: VerifiedSquare,
    val hit: Boolean,
    val destroyedShip: String? = null
)
