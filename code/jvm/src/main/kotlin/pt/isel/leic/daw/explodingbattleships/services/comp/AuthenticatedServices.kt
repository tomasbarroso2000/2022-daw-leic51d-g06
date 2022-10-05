package pt.isel.leic.daw.explodingbattleships.services.comp

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyInput
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.*

/**
 * Section of services that requires authentication
 */
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
     * @param token the user's token
     * @param lobbyInput the characteristics of the game the user wants to play
     * @return EnterLobbyOutput representing if the player was placed in queue
     */
    fun enterLobby(token: String?, lobbyInput: EnterLobbyInput) = doService(data) { transaction ->
        val playerId = computePlayer(transaction, token, data).id
        if (getPlayerGame(transaction, playerId, data) != null)
            throw AppException("Player already in a game", AppExceptionStatus.BAD_REQUEST)
        if (isPlayerInLobby(transaction, playerId, data))
            throw AppException("Player already in lobby", AppExceptionStatus.BAD_REQUEST)
        if (lobbyInput.width == null || lobbyInput.width < BOARD_MIN_WIDTH)
            throw AppException("Invalid board width", AppExceptionStatus.BAD_REQUEST)
        if (lobbyInput.height == null || lobbyInput.height < BOARD_MIN_HEIGHT)
            throw AppException("Invalid board height", AppExceptionStatus.BAD_REQUEST)
        if (lobbyInput.hitsPerRound == null || lobbyInput.hitsPerRound <= 0)
            throw AppException("Invalid hits per round", AppExceptionStatus.BAD_REQUEST)
        data.playersData.enterLobby(transaction, playerId, lobbyInput.width, lobbyInput.height, lobbyInput.hitsPerRound)
    }
}