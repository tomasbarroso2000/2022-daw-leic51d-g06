package pt.isel.leic.daw.explodingbattleships.domain

import java.time.Instant

/**
 * Represents a Lobby
 * @property id the lobby id
 * @property userId the user id
 * @property gameType the game type
 * @property enterTime the instant the user entered the lobby
 * @property gameId the new game id
 */
data class Lobby(
    val id: Int,
    val userId: Int,
    val gameType: String,
    val enterTime: Instant,
    val gameId: Int?
)