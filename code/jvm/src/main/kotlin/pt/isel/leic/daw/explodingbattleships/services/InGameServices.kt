package pt.isel.leic.daw.explodingbattleships.services

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.Hits
import pt.isel.leic.daw.explodingbattleships.domain.Layout
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare
import pt.isel.leic.daw.explodingbattleships.domain.getString
import pt.isel.leic.daw.explodingbattleships.domain.idlePlayer
import pt.isel.leic.daw.explodingbattleships.domain.otherPlayer
import pt.isel.leic.daw.explodingbattleships.domain.toVerifiedSquareOrNull
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus
import pt.isel.leic.daw.explodingbattleships.services.utils.checkCurrentPlayer
import pt.isel.leic.daw.explodingbattleships.services.utils.checkGameState
import pt.isel.leic.daw.explodingbattleships.services.utils.checkOrThrow
import pt.isel.leic.daw.explodingbattleships.services.utils.checkPlayerInGame
import pt.isel.leic.daw.explodingbattleships.services.utils.checkShipLayout
import pt.isel.leic.daw.explodingbattleships.services.utils.computePlayer
import pt.isel.leic.daw.explodingbattleships.services.utils.doService
import pt.isel.leic.daw.explodingbattleships.services.utils.executeHit
import pt.isel.leic.daw.explodingbattleships.services.utils.getPlayerGameOrThrow
import pt.isel.leic.daw.explodingbattleships.services.utils.squareInBoard

/**
 * Section of services that relates to in-game actions
 */
class InGameServices(private val data: Data) {
    /**
     * Defines a layout for a player in a game
     * @param token the player's token
     * @param layout the player's chosen layout
     * @return true if successful
     */
    fun defineLayout(token: String?, layout: Layout) = doService(data) { transaction ->
        val playerId = computePlayer(transaction, token, data).id
        val game = getPlayerGameOrThrow(transaction, playerId, data)
        checkPlayerInGame(game, playerId)
        checkGameState(game.state, "layout_definition")
        if (layout.ships == null)
            throw AppException("No ships provided", AppExceptionStatus.BAD_REQUEST)
        val verifiedShips = checkShipLayout(layout.ships, game.width, game.height)
        data.inGameData.defineLayout(transaction, game.id, playerId, verifiedShips)
    }

    /**
     * Sends the hits the user has thrown in his turn
     * @param token the user's token
     * @param hits the hits to be sent
     * @return every hit's outcome
     */
    fun sendHits(token: String?, hits: Hits) = doService(data) { transaction ->
        val playerId = computePlayer(transaction, token, data).id
        val game = getPlayerGameOrThrow(transaction, playerId, data)
        checkPlayerInGame(game, playerId)
        checkCurrentPlayer(game, playerId)
        checkGameState(game.state, "shooting")
        if (hits.squares.isNullOrEmpty())
            throw AppException("No squares provided", AppExceptionStatus.BAD_REQUEST)
        if (hits.squares.size > game.hitsPerRound)
            throw AppException("Invalid amount of hits", AppExceptionStatus.BAD_REQUEST)
        val hitSquares = data.gamesData.getHitSquares(transaction, game.id, game.idlePlayer()).toMutableSet()
        val verifiedSquares = mutableListOf<VerifiedSquare>()
        hits.squares.forEach { unverifiedSquare ->
            val verifiedSquare = unverifiedSquare.toVerifiedSquareOrNull()
                ?: throw AppException("Invalid square: ${unverifiedSquare.getString()}", AppExceptionStatus.BAD_REQUEST)
            checkOrThrow(!squareInBoard(verifiedSquare, game.width, game.height), "Invalid square: ${unverifiedSquare.getString()}")
            checkOrThrow(hitSquares.contains(verifiedSquare), "Square already hit: ${unverifiedSquare.getString()}")
            verifiedSquares.add(verifiedSquare)
        }
        executeHit(transaction, game, verifiedSquares, game.idlePlayer(), data)
    }

    /**
     * Shows the state of the player's or enemy's fleet
     * @param token the user token
     * @param isPlayer represents if it is the player or enemy fleet
     * @return the state of every ship
     */
    fun fleetState(token: String?, isPlayer: Boolean) = doService(data) { transaction ->
        val playerId = computePlayer(transaction, token, data).id
        val game = getPlayerGameOrThrow(transaction, playerId, data)
        if (isPlayer)
            data.inGameData.fleetState(transaction, game.id, playerId)
        else
            data.inGameData.fleetState(transaction, game.id, game.otherPlayer(playerId))
    }
}
