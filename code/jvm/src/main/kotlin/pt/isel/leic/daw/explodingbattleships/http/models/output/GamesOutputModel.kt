package pt.isel.leic.daw.explodingbattleships.http.models.output

import pt.isel.leic.daw.explodingbattleships.domain.AvailableGame
import pt.isel.leic.daw.explodingbattleships.domain.DataList

/**
 * Represents the information needed to show the user a game he is currently playing
 * @param type the game type
 * @param state the game state
 * @param opponent the player's opponent
 */
class GamesOutputModel(
    val games: DataList<AvailableGame>
)