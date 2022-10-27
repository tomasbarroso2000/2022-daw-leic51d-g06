package pt.isel.leic.daw.explodingbattleships.http.models.input

/**
 * Represents the information needed for entering a lobby
 * @param gameType the game type the user wants to play
 */
data class LobbyInputModel(
    val gameType: String
)