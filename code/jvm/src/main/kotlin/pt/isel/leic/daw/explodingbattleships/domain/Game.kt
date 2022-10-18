package pt.isel.leic.daw.explodingbattleships.domain

import java.time.Duration
import java.time.Instant

data class Game(
    val id: Int,
    val type: String,
    val state: String,
    val player1: Int,
    val player2: Int,
    val currPlayer: Int,
    val deadline: Duration
)

fun Game.idlePlayer() = if (currPlayer == player1) player2 else player1

fun Game.otherPlayer(playerId: Int) =
    if (player1 == playerId) player2
    else if (player2 == playerId) player1
    else throw IllegalArgumentException("Player not in game")
