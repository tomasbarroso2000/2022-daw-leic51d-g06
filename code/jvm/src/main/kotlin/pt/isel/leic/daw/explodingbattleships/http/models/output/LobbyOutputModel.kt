package pt.isel.leic.daw.explodingbattleships.http.models.output

data class LobbyOutputModel(
    val waitingForGame: Boolean,
    val gameId: Int?
)
