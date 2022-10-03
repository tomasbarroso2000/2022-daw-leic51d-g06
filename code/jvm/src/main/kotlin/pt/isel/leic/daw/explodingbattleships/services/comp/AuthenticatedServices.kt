package pt.isel.leic.daw.explodingbattleships.services.comp

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyInput
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.*

class AuthenticatedServices(private val data: Data) {

    fun enterLobby(token: String?, lobbyInput: EnterLobbyInput) = doService(data) { transaction ->
        val playerId = computePlayerId(transaction, token, data)
        if (lobbyInput.width < BOARD_MIN_WIDTH)
            throw AppException("Invalid board width", AppExceptionStatus.BAD_REQUEST)
        if (lobbyInput.height < BOARD_MIN_HEIGHT)
            throw AppException("Invalid board height", AppExceptionStatus.BAD_REQUEST)
        if (lobbyInput.hitsPerRound <= 0)
            throw AppException("Invalid hits per round", AppExceptionStatus.BAD_REQUEST)
        data.playersData.enterLobby(transaction, playerId, lobbyInput.width, lobbyInput.height, lobbyInput.hitsPerRound)
    }
}