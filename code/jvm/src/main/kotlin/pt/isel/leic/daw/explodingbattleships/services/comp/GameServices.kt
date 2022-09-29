package pt.isel.leic.daw.explodingbattleships.services.comp

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppExceptionStatus
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.doService

class GameServices(private val data: Data) {
    fun getNumberOfPlayedGames() = doService(data) { transaction ->
        data.gameData.getNumberOfPlayedGames(transaction)
    }

    fun getGameState(gameId: Int?) = doService(data) { transaction ->
        if (gameId == null || gameId <= 0)
            throw AppException("Invalid gameId", AppExceptionStatus.BAD_REQUEST)
        data.gameData.getGameState(transaction, gameId)
            ?: throw AppException("Game does not exist", AppExceptionStatus.NOT_FOUND)
    }
}
