package pt.isel.leic.daw.explodingbattleships.services.comp

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.DataDb
import pt.isel.leic.daw.explodingbattleships.domain.Hit
import pt.isel.leic.daw.explodingbattleships.domain.Layout
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        data.gameData.defineLayout(transaction, layout.gameId, playerId, layout.ships)
    }

    fun squareHit(hit: Hit) = doService(data) { transaction ->
        if (hit.gameId == null || hit.gameId <= 0)
            throw AppException("Invalid gameId", AppExceptionStatus.BAD_REQUEST)
        val game = data.gameData.getGame(transaction, hit.gameId)
            ?: throw AppException("Game does not exist", AppExceptionStatus.NOT_FOUND)
        if (game.state != "shooting")
            throw AppException("Invalid game state")
        if (hit.token.isNullOrBlank())
            throw AppException("No token provided", AppExceptionStatus.BAD_REQUEST)
        val playerId = data.playerData.getPlayerIdByToken(transaction, hit.token)
            ?: throw AppException("Invalid token", AppExceptionStatus.BAD_REQUEST)
        if (hit.player != playerId)
            throw AppException("Wrong game", AppExceptionStatus.BAD_REQUEST)
        if(hit.hit_timestamp.isNullOrBlank())
            throw AppException("No timestamp provided", AppExceptionStatus.BAD_REQUEST)
        if (hit.square == null)
            throw AppException("No square provided", AppExceptionStatus.BAD_REQUEST)
        if (!squareInBoard(hit.square, game.width, game.height))
            throw AppException("Invalid square", AppExceptionStatus.BAD_REQUEST)
        val ts = LocalDate.parse(hit.hit_timestamp, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        data.gameData.squareHit(transaction, hit.square, ts, hit.player, hit.gameId)
    }
}

fun main() {
    val layout = Layout(
        1, "123", listOf(
            Ship("carrier", Square('a', 1), "horizontal"),
            Ship("battleship", Square('b', 1), "vertical"),
            Ship("submarine", Square('b', 2), "horizontal"),
            Ship("cruiser", Square('c', 2), "horizontal"),
            Ship("destroyer", Square('d', 2), "vertical")
        )
    )
    GameServices(DataDb()).defineLayout(layout)
}
