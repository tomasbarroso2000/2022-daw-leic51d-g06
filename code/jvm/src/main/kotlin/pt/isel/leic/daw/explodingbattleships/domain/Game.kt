package pt.isel.leic.daw.explodingbattleships.domain

import java.time.Instant

/**
 * Represents a game
 * @property id the game id
 * @property type the game type
 * @property state the game state
 * @property player1 one of the users
 * @property player2 the other user
 * @property currPlayer the user currently playing
 * @property startedAt the instant the current turn started at
 */
data class Game(
    val id: Int,
    val type: String,
    val state: String,
    val player1: Int,
    val player2: Int,
    val currPlayer: Int,
    val startedAt: Instant
) {
    /**
     * Gets the user not currently playing
     */
    fun idlePlayer() = if (currPlayer == player1) player2 else player1

    /**
     * Gets the player in the game not corresponding to the id passed as a parameter
     * @param playerId the user id
     */
    fun otherPlayer(playerId: Int) =
        if (player1 == playerId) {
            player2
        } else if (player2 == playerId) {
            player1
        } else {
            throw IllegalArgumentException("Player not in game")
        }
}