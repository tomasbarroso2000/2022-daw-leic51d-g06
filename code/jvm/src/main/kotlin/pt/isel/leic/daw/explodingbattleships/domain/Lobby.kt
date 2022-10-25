package pt.isel.leic.daw.explodingbattleships.domain

import java.time.Instant

data class EnterLobbyInput(val gameType: String)

data class EnterLobbyOutput(
    val waitingForGame: Boolean,
    val gameId: Int?
)

data class Lobby(
    val player: Int,
    val gameType: String,
    val enterTime: Instant
)
