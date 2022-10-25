package pt.isel.leic.daw.explodingbattleships.http.models.output

import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.Square
import java.time.Instant

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