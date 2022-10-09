package pt.isel.leic.daw.explodingbattleships.domain

data class Game(
    val id: Int,
    val width: Int,
    val height: Int,
    val hitsPerRound: Int,
    val state: String,
    val player1: Int,
    val player2: Int,
    val currPlayer: Int
)

fun Game.idlePlayer() = if (currPlayer == player1) player2 else player1

fun Game.otherPlayer(playerId: Int) =
    if (player1 == playerId) player2
    else if (player2 == playerId) player1
    else throw IllegalArgumentException("Player not in game")
