package pt.isel.leic.daw.explodingbattleships.domain

import java.time.Instant

data class EnterLobbyOutput(
    val waitingForGame: Boolean,
    val gameId: Int?
)

data class Lobby(
    val userId: Int,
    val gameType: String,
    val enterTime: Instant
)
