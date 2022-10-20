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
     * Sends a layout for a player in a game and
     * starts a game if both players have successfully defined their layout
     * @param player the player
     * @param layout the player's chosen layout
     * @return true if successful
     */
    fun sendLayout(player: Player, layout: Layout) = doService(data) { transaction ->
        val game = computeGame(transaction, layout.gameId, data)
        checkPlayerInGame(game, player.id)
        checkGameState(game.state, "layout_definition")
        if (layout.ships == null)
            throw AppException("No ships provided", AppExceptionStatus.BAD_REQUEST)
        if (data.inGameData.hasShips(transaction, player.id, game.id))
            throw AppException("Layout already defined", AppExceptionStatus.BAD_REQUEST)
        val verifiedShips = checkShipLayout(game.type, layout.ships)
        data.inGameData.defineLayout(transaction, game.id, player.id, verifiedShips)
        if (data.inGameData.checkEnemyDone(transaction, game.id, player.id))
            data.inGameData.startGame(transaction, game.id, player.id)
        else
            LayoutOutcome(LayoutOutcomeStatus.WAITING)
    }

    /**
     * Sends the hits the user has thrown in his turn
     * @param player the player
     * @param hits the hits to be sent
     * @return every hit's outcome
     */
    fun sendHits(player: Player, hits: Hits) = doService(data) { transaction ->
        val game = computeGame(transaction, hits.gameId, data)
        checkPlayerInGame(game, player.id)
        checkCurrentPlayer(game, player.id)
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
            checkOrThrowBadRequest(!squareInBoard(verifiedSquare, gameType.boardSize), "Invalid square: ${unverifiedSquare.getString()}")
            checkOrThrowBadRequest(hitSquares.contains(verifiedSquare), "Square already hit: ${unverifiedSquare.getString()}")
            verifiedSquares.add(verifiedSquare)
        }
        val hitsOutcome = executeHit(transaction, game, verifiedSquares, game.idlePlayer(), data)
        if (hitsOutcome.win) {
            data.inGameData.setGameStateCompleted(transaction, game.id)
            hitsOutcome
        } else {
            hitsOutcome
        }
    }


    /**
     * Shows the state of the player's or enemy's fleet
     * @param player the player
     * @param fleet represents the fleet that is being requested
     * @return the state of every ship
     */
    fun fleetState(player: Player, fleet: Fleet) = doService(data) { transaction ->
        val game = computeGame(transaction, fleet.gameId, data)
        checkPlayerInGame(game, player.id)
        if (fleet.myFleet == null)
            throw AppException("Fleet not specified", AppExceptionStatus.BAD_REQUEST)
        if (fleet.myFleet)
            data.inGameData.fleetState(transaction, game.id, player.id)
        else
            data.inGameData.fleetState(transaction, game.id, game.otherPlayer(player.id))
    }

}
