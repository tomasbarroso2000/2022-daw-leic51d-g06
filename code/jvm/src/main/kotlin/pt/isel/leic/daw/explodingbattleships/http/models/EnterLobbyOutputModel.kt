package pt.isel.leic.daw.explodingbattleships.http.models

data class EnterLobbyOutputModel (
    val waitingForGame: Boolean,
    val gameId: Int?
)