package pt.isel.leic.daw.explodingbattleships.http.models.input

import pt.isel.leic.daw.explodingbattleships.domain.Square

/**
 * Represents the information needed for sending hits
 * @param gameId the game id
 * @param squares the squares hit
 */
data class HitsInputModel(
    val gameId: Int,
    val squares: List<Square>
)