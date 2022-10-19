package pt.isel.leic.daw.explodingbattleships.http

import org.springframework.web.util.UriTemplate
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.GameState
import java.net.URI

object Uris {
    const val BASE_PATH = "/api/"

    //authenticated
    const val PLAYER_INFO = "player/info"
    const val ENTER_LOBBY = "lobby/enter"

    //in-game
    const val SEND_HITS = "games/hit"
    const val DEFINE_LAYOUT = "games/layout"
    const val PLAYER_FLEET_STATE = "games/fleet/player/{gameId}"
    const val ENEMY_FLEET_STATE = "games/fleet/enemy/{gameId}"

    //unauthenticated
    const val CREATE_PLAYER = "players"
    const val RANKINGS = "rankings"
    const val NUMBER_OF_PLAYED_GAMES = "games/total"
    const val GAME_STATE = "games/state/{gameId}"

    fun home(): URI = URI(BASE_PATH)

    //authenticated
    fun playerInfo(): URI = URI(PLAYER_INFO)
    fun enterLobby(): URI = URI(ENTER_LOBBY)

    //in-game
    fun sendHits(): URI = URI(SEND_HITS)
    fun defineLayout(): URI = URI(DEFINE_LAYOUT)
    fun playerFleetState(gameId: Int): URI = UriTemplate(PLAYER_FLEET_STATE).expand(gameId)
    fun enemyFleetState(gameId: Int): URI = UriTemplate(ENEMY_FLEET_STATE).expand(gameId)

    //unauthenticated
    fun createPlayer(): URI = URI(CREATE_PLAYER)
    fun rankings(): URI = URI(RANKINGS)
    fun numberOfPlayedGames(): URI = URI(NUMBER_OF_PLAYED_GAMES)
    fun gameState(gameId: Int): URI = UriTemplate(GAME_STATE).expand(gameId)
}