package pt.isel.leic.daw.explodingbattleships.services.comp

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.DataDb
import pt.isel.leic.daw.explodingbattleships.domain.Hits
import pt.isel.leic.daw.explodingbattleships.domain.Layout
import pt.isel.leic.daw.explodingbattleships.domain.UnverifiedSquare
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare
import pt.isel.leic.daw.explodingbattleships.domain.otherPlayer
import pt.isel.leic.daw.explodingbattleships.domain.toVerifiedSquareOrNull
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppExceptionStatus
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.checkCurrentPlayer
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.checkGameState
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.checkOrThrow
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.checkPlayerInGame
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.checkShipLayout
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.computeGame
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.computePlayerId
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.doService
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.squareInBoard

class InGameServices(private val data: Data) {
    fun defineLayout(token: String?, layout: Layout) = doService(data) { transaction ->
        val playerId = computePlayerId(transaction, token, data)
        val game = computeGame(transaction, layout.gameId, data)
        checkGameState(game.state, "layout_definition")
        checkPlayerInGame(game, playerId)
        if (layout.ships == null)
            throw AppException("No ships provided", AppExceptionStatus.BAD_REQUEST)
        val verifiedShips = checkShipLayout(layout.ships, game.width, game.height)
        data.inGameData.defineLayout(transaction, game.id, playerId, verifiedShips)
    }

    fun sendHits(token: String?, hits: Hits) = doService(data) { transaction ->
        val playerId = computePlayerId(transaction, token, data)
        val game = computeGame(transaction, hits.gameId, data)
        checkGameState(game.state, "shooting")
        checkPlayerInGame(game, playerId)
        checkCurrentPlayer(game, playerId)
        if (hits.squares == null)
            throw AppException("No squares provided", AppExceptionStatus.BAD_REQUEST)
        if (hits.squares.size != game.hitsPerRound)
            throw AppException("Invalid amount of hits", AppExceptionStatus.BAD_REQUEST)
        val hitSquares = data.gamesData.getHitSquares(transaction, game.id, game.otherPlayer())?.toMutableSet()
            ?: throw AppException("Error")
        val verifiedSquares = mutableListOf<VerifiedSquare>()
        hits.squares.forEach { unverifiedSquare ->
            val verifiedSquare = unverifiedSquare.toVerifiedSquareOrNull()
                ?: throw AppException("Invalid square", AppExceptionStatus.BAD_REQUEST)
            checkOrThrow(!squareInBoard(verifiedSquare, game.width, game.height), "Invalid square")
            checkOrThrow(hitSquares.contains(verifiedSquare), "Square already hit")
            hitSquares.add(verifiedSquare)
            verifiedSquares.add(verifiedSquare)
        }
        data.inGameData.sendHits(transaction, game.id, game.otherPlayer(), verifiedSquares)
    }
}

fun main() {
    /*
    val layout = Layout(1, listOf(
            Ship("carrier", Square('a', 1), "horizontal"),
            Ship("battleship", Square('b', 1), "vertical"),
            Ship("submarine", Square('b', 2), "horizontal"),
            Ship("cruiser", Square('c', 2), "horizontal"),
            Ship("destroyer", Square('d', 2), "vertical")
        )
    )
    GameServices(DataDb()).defineLayout("123", layout)
     */
    val hits = Hits(1, listOf(UnverifiedSquare('a', 1)))
    println(InGameServices(DataDb()).sendHits("123", hits))
}