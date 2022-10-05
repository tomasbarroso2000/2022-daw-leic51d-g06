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
        // why not use gatPlayerGame? layout would stop needing to have gameId
        val game = computeGame(transaction, layout.gameId, data)
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
     * @return List<HitOutcome> Representing the hit's outcome
     */
    fun sendHits(token: String?, hits: Hits) = doService(data) { transaction ->
        val playerId = computePlayer(transaction, token, data).id
        // why not use gatPlayerGame? hits would stop needing to have gameId
        val game = computeGame(transaction, hits.gameId, data)
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
     * @return List<ShipState>
     */
    fun fleetState(token: String?, isPlayer: Boolean) = doService(data) { transaction ->
        val playerId = computePlayer(transaction, token, data).id
        val game = getPlayerGame(transaction, playerId, data)
            ?: throw AppException("Player not in game", AppExceptionStatus.BAD_REQUEST)
        if (isPlayer)
            data.inGameData.fleetState(transaction, game.id, playerId)
        else
            data.inGameData.fleetState(transaction, game.id, game.otherPlayer(playerId))
    }
}