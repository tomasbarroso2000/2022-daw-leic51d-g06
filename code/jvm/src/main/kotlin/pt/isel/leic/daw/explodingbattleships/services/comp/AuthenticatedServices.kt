package pt.isel.leic.daw.explodingbattleships.services.comp

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyInput
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.*

/**
 * Section of services that requires authentication
 */
class AuthenticatedServices(private val data: Data) {
    fun getPlayerInfo(token: String?) = doService(data) { transaction ->
        computePlayer(transaction, token, data)
    }

    fun enterLobby(token: String?, lobbyInput: EnterLobbyInput) = doService(data) { transaction ->
        val playerId = computePlayer(transaction, token, data).id
        if (getPlayerGame(transaction, playerId, data) != null)
            throw AppException("Player already in a game", AppExceptionStatus.BAD_REQUEST)
        if (isPlayerInLobby(transaction, playerId, data))
            throw AppException("Player already in lobby", AppExceptionStatus.BAD_REQUEST)
        if (lobbyInput.width < BOARD_MIN_WIDTH)
            throw AppException("Invalid board width", AppExceptionStatus.BAD_REQUEST)
        if (lobbyInput.height < BOARD_MIN_HEIGHT)
            throw AppException("Invalid board height", AppExceptionStatus.BAD_REQUEST)
        if (lobbyInput.hitsPerRound <= 0)
            throw AppException("Invalid hits per round", AppExceptionStatus.BAD_REQUEST)
        data.playersData.enterLobby(transaction, playerId, lobbyInput.width, lobbyInput.height, lobbyInput.hitsPerRound)
    }
}