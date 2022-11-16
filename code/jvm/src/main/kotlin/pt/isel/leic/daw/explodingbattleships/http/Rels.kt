package pt.isel.leic.daw.explodingbattleships.http

import pt.isel.leic.daw.explodingbattleships.infra.LinkRelation

/**
 * All the relation links needed
 */
object Rels {
    val NR_OF_TOTAL_GAMES = LinkRelation("nr_of_played_games")
    val RANKINGS = LinkRelation("rankings")
    val SELF = LinkRelation("self")
    val HOME = LinkRelation("home")
    val GAME = LinkRelation("game")
    val USER = LinkRelation("user")
    val ENTERED_GAME = LinkRelation("entered_game")
    val GAMES = LinkRelation("games")
    val GAME_TYPES = LinkRelation("game_types")
}