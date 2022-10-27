package pt.isel.leic.daw.explodingbattleships.http.models.output

/**
 * Represents the information needed to show the user the enter-lobby output
 * @param waitingForGame if the player is waiting in a lobby
 * @param gameId the game id
 */
data class LobbyOutputModel(
    val waitingForGame: Boolean,
    val gameId: Int?
)