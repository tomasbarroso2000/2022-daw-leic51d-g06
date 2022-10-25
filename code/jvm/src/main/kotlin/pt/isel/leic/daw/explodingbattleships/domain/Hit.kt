package pt.isel.leic.daw.explodingbattleships.domain

import java.time.Instant

data class Hits(val gameId: Int?, val squares: List<UnverifiedSquare>?)

data class HitOutcome(
    val square: VerifiedSquare,
    val hit: Boolean,
    val destroyedShip: String? = null
)

data class HitsOutcome(
    val hitsOutcome: List<HitOutcome>,
    val win: Boolean
)

data class Hit(
    val square: String,
    val hitTimestamp: Instant,
    val onShip: Boolean,
    val player: Int,
    val game: Int
)