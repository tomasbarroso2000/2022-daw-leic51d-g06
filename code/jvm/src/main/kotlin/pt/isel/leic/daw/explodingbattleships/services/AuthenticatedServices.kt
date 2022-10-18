package pt.isel.leic.daw.explodingbattleships.services

import org.springframework.stereotype.Component
import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyInput
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.services.utils.*

/**
 * Section of services that requires authentication
 */
@Component
class AuthenticatedServices(private val data: Data) {

    /**
     * Get the player with the token passed as parameter
     * @param token the user's token
     * @return the player
     */
    fun getPlayerInfo(token: String?) = doService(data) { transaction ->
        computePlayer(transaction, token, data)
    }

    /**
     * Places the user in a lobby or in a game if there is already someone waiting in the lobby with the same game characteristics
     * @param lobbyInput the characteristics of the game the user wants to play
     * @return a [EnterLobbyOutput] representing if the player was placed in queue
     */
    fun enterLobby(player: Player, lobbyInput: EnterLobbyInput) = doService(data) { transaction ->
        if (lobbyInput.gameType == null || isGameTypeInvalid(lobbyInput.gameType))
            throw AppException("Invalid game type", AppExceptionStatus.BAD_REQUEST)
        enterLobbyOrCreateGame(transaction, player.id, lobbyInput.gameType, data)
    }
}
