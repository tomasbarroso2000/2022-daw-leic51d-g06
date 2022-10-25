package pt.isel.leic.daw.explodingbattleships.http.models

import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.ShipDto
import pt.isel.leic.daw.explodingbattleships.domain.Square
import java.time.Instant

data class GameOutputModel(
    val id: Int,
    val type: String,
    val state: String,
    val opponent: Int,
    val playing: Boolean,
    val startedAt: Instant,
    val fleet: List<ShipDto>,
    val takenHits: List<Square>,
    val enemySunkFleet: List<ShipDto>,
    val hits: List<Square>,
    val misses: List<Square>
)