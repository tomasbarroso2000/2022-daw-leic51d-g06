package pt.isel.leic.daw.explodingbattleships.http.models.output

data class EnterLobbyOutputModel (
    val waitingForGame: Boolean,
    val gameId: Int?
)