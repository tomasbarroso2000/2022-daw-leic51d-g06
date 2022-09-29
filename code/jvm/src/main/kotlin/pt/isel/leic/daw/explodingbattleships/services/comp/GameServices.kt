package pt.isel.leic.daw.explodingbattleships.services.comp

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.Layout
import pt.isel.leic.daw.explodingbattleships.domain.Ship
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

    fun defineLayout(layout: Layout) = doService(data) { transaction ->
        if (layout.gameId == null || layout.gameId <= 0)
            throw AppException("Invalid gameId", AppExceptionStatus.BAD_REQUEST)
        val game = data.gameData.getGame(transaction, layout.gameId)
            ?: throw AppException("Game does not exist", AppExceptionStatus.NOT_FOUND)
        if (game.state != "layout_definition")
            throw AppException("Invalid game state")
        if (layout.token.isNullOrBlank())
            throw AppException("No token provided", AppExceptionStatus.BAD_REQUEST)
        val playerId = data.playerData.getPlayerIdByToken(transaction, layout.token)
            ?: throw AppException("Invalid token", AppExceptionStatus.BAD_REQUEST)
        if (game.player1 != playerId && game.player2 != playerId)
            throw AppException("Wrong game", AppExceptionStatus.BAD_REQUEST)
        if (layout.ships == null)
            throw AppException("No ships provided", AppExceptionStatus.BAD_REQUEST)
        checkShipLayout(layout.ships, game.width, game.height)
    }
}
