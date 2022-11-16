package pt.isel.leic.daw.explodingbattleships.http.models.output

/**
 * Represents the information needed to show the user a game he is currently playing
 * @param games the games list
 * @param hasMore if there are more elements
 */
class GamesOutputModel(
    val games: List<GameOutputModel>,
    val hasMore: Boolean
)