package pt.isel.leic.daw.explodingbattleships.http

import pt.isel.leic.daw.explodingbattleships.infra.LinkRelation

/**
 * All the relation links needed
 */
object Rels {
    val LAYOUT = LinkRelation("layout")
    val TOKEN = LinkRelation("token")
    val SEND_HITS = LinkRelation("send_hits")
    val NR_OF_TOTAL_GAMES = LinkRelation("nr_of_played_games")
    val ENTER_LOBBY = LinkRelation("enter_lobby")
    val CREATE_USER = LinkRelation("create_user")
    val RANKINGS = LinkRelation("rankings")
    val SELF = LinkRelation("self")
    val HOME = LinkRelation("home")
    val GAME = LinkRelation("game")
    val USER = LinkRelation("user")
}