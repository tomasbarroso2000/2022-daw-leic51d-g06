package pt.isel.leic.daw.explodingbattleships.services

import org.springframework.stereotype.Component
import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.*
import pt.isel.leic.daw.explodingbattleships.services.utils.*

/**
 * Section of services that relates to in-game actions
 */
@Component
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
        checkPlayerInGame(game, playerId)
        checkGameState(game.state, "layout_definition")
        if (layout.ships == null)
            throw AppException("No ships provided", AppExceptionStatus.BAD_REQUEST)
        // verificar se o jogador ja submeteu um layout
        if (data.inGameData.hasShips(transaction, playerId, game.id))
            throw AppException("Already defined layout")
        val verifiedShips = checkShipLayout(game.type, layout.ships)
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
        val game = computeGame(transaction, hits.gameId, data)
        checkPlayerInGame(game, playerId)
        checkCurrentPlayer(game, playerId)
        checkGameState(game.state, "shooting")
        val gameType = game.type.toGameType()
            ?: throw AppException("Game type not registered")
        if (hits.squares.isNullOrEmpty())
            throw AppException("No squares provided", AppExceptionStatus.BAD_REQUEST)
        if (hits.squares.size > gameType.shotsPerRound)
            throw AppException("Invalid amount of hits", AppExceptionStatus.BAD_REQUEST)
        val hitSquares = data.gamesData.getHitSquares(transaction, game.id, game.idlePlayer()).toMutableSet()
        val verifiedSquares = mutableListOf<VerifiedSquare>()
        hits.squares.forEach { unverifiedSquare ->
            val verifiedSquare = unverifiedSquare.toVerifiedSquareOrNull()
                ?: throw AppException("Invalid square: ${unverifiedSquare.getString()}", AppExceptionStatus.BAD_REQUEST)
            checkOrThrow(!squareInBoard(verifiedSquare, gameType.boardSize), "Invalid square: ${unverifiedSquare.getString()}")
            checkOrThrow(hitSquares.contains(verifiedSquare), "Square already hit: ${unverifiedSquare.getString()}")
            verifiedSquares.add(verifiedSquare)
        }
        executeHit(transaction, game, verifiedSquares, game.idlePlayer(), data)
    }


    /**
     * Shows the state of the player's or enemy's fleet
     * @param token the user token
     * @param fleet represents the fleet that is being requested
     * @return the state of every ship
     */
    fun fleetState(token: String?, fleet: Fleet) = doService(data) { transaction ->
        val playerId = computePlayer(transaction, token, data).id
        val game = computeGame(transaction, fleet.gameId, data)
        checkPlayerInGame(game, playerId)
        if (fleet.myFleet == null)
            throw AppException("Fleet not specified", AppExceptionStatus.BAD_REQUEST)
        if (fleet.myFleet)
            data.inGameData.fleetState(transaction, game.id, playerId)
        else
            data.inGameData.fleetState(transaction, game.id, game.otherPlayer(playerId))
    }

}
