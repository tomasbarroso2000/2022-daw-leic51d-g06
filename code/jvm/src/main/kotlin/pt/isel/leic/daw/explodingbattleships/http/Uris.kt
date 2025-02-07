package pt.isel.leic.daw.explodingbattleships.http

import org.springframework.web.util.UriTemplate
import java.net.URI

/**
 * All the different Uris needed
 */
object Uris {
    const val BASE_PATH = "/api/"

    fun home(): URI = URI(BASE_PATH)

    object Users {
        const val HOME = "me"
        const val CREATE = "users"
        const val TOKEN = "users/token"
        const val ENTER_LOBBY = "lobby"
        const val ENTERED_GAME = "lobby/{lobbyId}"
        const val RANKINGS = "users/rankings"

        fun home(): URI = URI(BASE_PATH + HOME)
        fun createUser(): URI = URI(BASE_PATH + CREATE)
        fun createToken(): URI = URI(BASE_PATH + TOKEN)
        fun enterLobby(): URI = URI(BASE_PATH + ENTER_LOBBY)
        fun enteredGame(lobbyId: Int): URI = UriTemplate(BASE_PATH + ENTERED_GAME).expand(lobbyId)
        fun rankings(): URI = URI(BASE_PATH + RANKINGS)
    }

    object Games {
        const val GAMES = "games"
        const val GAME_TYPES = "games/types"
        const val GAME_INFO = "games/info/{gameId}"
        const val SEND_HITS = "games/hit"
        const val DEFINE_LAYOUT = "games/layout"
        const val PLAYER_FLEET = "games/fleet/player/{gameId}"
        const val ENEMY_FLEET = "games/fleet/enemy/{gameId}"
        const val NR_OF_GAMES = "games/total"
        const val GAME_STATE = "games/state/{gameId}"
        const val FORFEIT = "games/forfeit"

        fun games(): URI = URI(BASE_PATH + GAMES)
        fun gameTypes(): URI = URI(BASE_PATH + GAME_TYPES)
        fun gameInfo(gameId: Int): URI = UriTemplate(BASE_PATH + GAME_INFO).expand(gameId)
        fun sendHits(): URI = URI(BASE_PATH + SEND_HITS)
        fun defineLayout(): URI = URI(BASE_PATH + DEFINE_LAYOUT)
        fun playerFleet(gameId: Int): URI = UriTemplate(BASE_PATH + PLAYER_FLEET).expand(gameId)
        fun enemyFleet(gameId: Int): URI = UriTemplate(BASE_PATH + ENEMY_FLEET).expand(gameId)
        fun nrOfGames(): URI = URI(BASE_PATH + NR_OF_GAMES)
        fun gameState(gameId: Int): URI = UriTemplate(BASE_PATH + GAME_STATE).expand(gameId)
        fun forfeit(): URI = URI(BASE_PATH + FORFEIT)
    }
}