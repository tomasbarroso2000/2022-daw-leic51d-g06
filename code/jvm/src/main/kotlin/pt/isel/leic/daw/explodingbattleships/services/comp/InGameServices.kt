package pt.isel.leic.daw.explodingbattleships.services.comp

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.*
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.*

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
        val game = computeGame(transaction, layout.gameId, data)
        checkGameState(game.state, "layout_definition")
        checkPlayerInGame(game, playerId)
        if (layout.ships == null)
            throw AppException("No ships provided", AppExceptionStatus.BAD_REQUEST)
        val verifiedShips = checkShipLayout(layout.ships, game.width, game.height)
        data.inGameData.defineLayout(transaction, game.id, playerId, verifiedShips)
    }

    fun sendHits(token: String?, hits: Hits) = doService(data) { transaction ->
        val playerId = computePlayer(transaction, token, data).id
        val game = computeGame(transaction, hits.gameId, data)
        checkGameState(game.state, "shooting")
        checkPlayerInGame(game, playerId)
        checkCurrentPlayer(game, playerId)
        if (hits.squares == null)
            throw AppException("No squares provided", AppExceptionStatus.BAD_REQUEST)
        if (hits.squares.size != game.hitsPerRound)
            throw AppException("Invalid amount of hits", AppExceptionStatus.BAD_REQUEST)
        val hitSquares = data.gamesData.getHitSquares(transaction, game.id, game.idlePlayer()).toMutableSet()
        val verifiedSquares = mutableListOf<VerifiedSquare>()
        hits.squares.forEach { unverifiedSquare ->
            val verifiedSquare = unverifiedSquare.toVerifiedSquareOrNull()
                ?: throw AppException("Invalid square", AppExceptionStatus.BAD_REQUEST)
            checkOrThrow(!squareInBoard(verifiedSquare, game.width, game.height), "Invalid square")
            checkOrThrow(hitSquares.contains(verifiedSquare), "Square already hit")
            verifiedSquares.add(verifiedSquare)
        }

        //data.inGameData.sendHits(transaction, game.id, game.idlePlayer(), verifiedSquares)
        executeHit(transaction, game, verifiedSquares, playerId, data)
    }

    fun playerFleetState(token: String?) = doService(data) { transaction ->
        val playerId = computePlayer(transaction, token, data).id
        val game = getPlayerGame(transaction, playerId, data)
            ?: throw AppException("Player not in game", AppExceptionStatus.NOT_FOUND)
        data.inGameData.fleetState(transaction, game.id, playerId)
    }

    fun enemyFleetState(token: String?) = doService(data) { transaction ->
        val playerId = computePlayer(transaction, token, data).id
        val game = getPlayerGame(transaction, playerId, data)
            ?: throw AppException("Player not in game", AppExceptionStatus.NOT_FOUND)
        data.inGameData.fleetState(transaction, game.id, game.otherPlayer(playerId))
    }
}