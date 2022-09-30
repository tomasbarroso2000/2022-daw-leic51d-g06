package pt.isel.leic.daw.explodingbattleships.services.comp

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.DataDb
import pt.isel.leic.daw.explodingbattleships.domain.Hits
import pt.isel.leic.daw.explodingbattleships.domain.Layout
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.domain.otherPlayer
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
        if (layout.token.isNullOrBlank())
            throw AppException("No token provided", AppExceptionStatus.BAD_REQUEST)
        val playerId = data.playerData.getPlayerIdByToken(transaction, layout.token)
            ?: throw AppException("Invalid token", AppExceptionStatus.BAD_REQUEST)
        if (layout.gameId == null || layout.gameId <= 0)
            throw AppException("Invalid gameId", AppExceptionStatus.BAD_REQUEST)
        val game = data.gameData.getGame(transaction, layout.gameId)
            ?: throw AppException("Game does not exist", AppExceptionStatus.NOT_FOUND)
        if (game.state != "layout_definition")
            throw AppException("Invalid game state", AppExceptionStatus.BAD_REQUEST)
        if (game.player1 != playerId && game.player2 != playerId)
            throw AppException("Wrong game", AppExceptionStatus.BAD_REQUEST)
        if (layout.ships == null)
            throw AppException("No ships provided", AppExceptionStatus.BAD_REQUEST)
        checkShipLayout(layout.ships, game.width, game.height)
        data.gameData.defineLayout(transaction, layout.gameId, playerId, layout.ships)
    }

    fun sendHits(hits: Hits) = doService(data) { transaction ->
        if (hits.token.isNullOrBlank())
            throw AppException("No token provided", AppExceptionStatus.BAD_REQUEST)
        val playerId = data.playerData.getPlayerIdByToken(transaction, hits.token)
            ?: throw AppException("Invalid token", AppExceptionStatus.BAD_REQUEST)
        if (hits.gameId == null || hits.gameId <= 0)
            throw AppException("Invalid gameId", AppExceptionStatus.BAD_REQUEST)
        val game = data.gameData.getGame(transaction, hits.gameId)
            ?: throw AppException("Game does not exist", AppExceptionStatus.NOT_FOUND)
        if (game.state != "shooting")
            throw AppException("Invalid game state")
        if (game.player1 != playerId && game.player2 != playerId)
            throw AppException("Wrong game", AppExceptionStatus.BAD_REQUEST)
        if (game.currPlayer != playerId)
            throw AppException("Not your turn", AppExceptionStatus.BAD_REQUEST)
        if (hits.squares == null)
            throw AppException("No squares provided", AppExceptionStatus.BAD_REQUEST)
        if (hits.squares.size != game.hitsPerRound)
            throw AppException("Invalid amount of hits", AppExceptionStatus.BAD_REQUEST)
        val hitSquares: MutableSet<Square> =
            data.gameData.hitSquares(transaction, hits.gameId, game.otherPlayer())?.toMutableSet()
                ?: throw AppException("Error")
        hits.squares.forEach { square ->
            if (!squareInBoard(square, game.width, game.height))
                throw AppException("Invalid square", AppExceptionStatus.BAD_REQUEST)
            if (hitSquares.contains(square))
                throw AppException("Square already hit", AppExceptionStatus.BAD_REQUEST)
            hitSquares.add(square)
        }
        data.gameData.sendHits(transaction, hits.gameId, game.otherPlayer(), hits.squares)
    }
}

fun main() {
    /*
    val layout = Layout("123", 1, listOf(
            Ship("carrier", Square('a', 1), "horizontal"),
            Ship("battleship", Square('b', 1), "vertical"),
            Ship("submarine", Square('b', 2), "horizontal"),
            Ship("cruiser", Square('c', 2), "horizontal"),
            Ship("destroyer", Square('d', 2), "vertical")
        )
    )
    GameServices(DataDb()).defineLayout(layout)
     */
    val hits = Hits("123", 1, listOf(Square('a', 1)))
    GameServices(DataDb()).sendHits(hits)
}
