package pt.isel.leic.daw.explodingbattleships.domain

import java.time.Instant

data class Game(
    val id: Int,
    val type: String,
    val state: String,
    val player1: Int,
    val player2: Int,
    val currPlayer: Int,
    val startedAt: Instant
)

fun Game.idlePlayer() = if (currPlayer == player1) player2 else player1

fun Game.otherPlayer(playerId: Int) =
    if (player1 == playerId) {
        player2
    } else if (player2 == playerId) {
        player1
    } else {
        throw IllegalArgumentException("Player not in game")
    }

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
