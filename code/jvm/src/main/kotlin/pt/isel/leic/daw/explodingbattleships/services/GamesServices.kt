package pt.isel.leic.daw.explodingbattleships.services

import org.springframework.stereotype.Component
import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.FullGameInfo
import pt.isel.leic.daw.explodingbattleships.domain.LayoutOutcomeStatus
import pt.isel.leic.daw.explodingbattleships.domain.ShipCreationInfo
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.domain.getString
import pt.isel.leic.daw.explodingbattleships.domain.idlePlayer
import pt.isel.leic.daw.explodingbattleships.domain.otherPlayer
import pt.isel.leic.daw.explodingbattleships.domain.toShipState
import pt.isel.leic.daw.explodingbattleships.domain.toSquareOrThrow
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus
import pt.isel.leic.daw.explodingbattleships.services.utils.checkCurrentPlayer
import pt.isel.leic.daw.explodingbattleships.services.utils.checkGameState
import pt.isel.leic.daw.explodingbattleships.services.utils.checkOrThrowBadRequest
import pt.isel.leic.daw.explodingbattleships.services.utils.checkPlayerInGame
import pt.isel.leic.daw.explodingbattleships.services.utils.checkShipLayout
import pt.isel.leic.daw.explodingbattleships.services.utils.computeGame
import pt.isel.leic.daw.explodingbattleships.services.utils.doService
import pt.isel.leic.daw.explodingbattleships.services.utils.executeHit
import pt.isel.leic.daw.explodingbattleships.services.utils.squareInBoard
import pt.isel.leic.daw.explodingbattleships.services.utils.toGameTypeOrNull
import pt.isel.leic.daw.explodingbattleships.services.utils.toGameTypeOrThrow
import java.lang.IllegalArgumentException
import java.time.Instant

@Component
class GamesServices(private val data: Data) {

    /**
     * Gets all the information of a game
     * @param userId the id of the user
     * @param gameId the id of the game
     * @return all the information of the game
     */
    fun getGame(userId: Int, gameId: Int) = doService(data) { transaction ->
        var game = computeGame(transaction, gameId, data)
        val gameType = game.type.toGameTypeOrThrow()
        val isTimeOver = game
            .startedAt
            .plusSeconds(gameType.shootingTimeInSecs.toLong()) <= Instant.now()
        if (isTimeOver) {
            data.gamesData.changeCurrPlayer(transaction, game.id, game.idlePlayer())
            game = data.gamesData.getGame(transaction, gameId) ?: throw IllegalArgumentException("Get game is null")
        }
        val opponentId = game.otherPlayer(userId)
        val playing = game.currPlayer == userId
        val playerFleet = data.shipsData.getFleet(transaction, game.id, userId)
        val takenHitsSquares = data.hitsData.getHits(transaction, game.id, userId).map { it.square.toSquareOrThrow() }
        val enemyFleet = data.shipsData.getFleet(transaction, game.id, opponentId).filter { it.destroyed }
        val sentHits = data.hitsData.getHits(transaction, game.id, opponentId)
        val hits = sentHits.filter { it.onShip }.map { it.square.toSquareOrThrow() }
        val misses = sentHits.filter { !it.onShip }.map { it.square.toSquareOrThrow() }
        FullGameInfo(
            game,
            opponentId,
            playing,
            playerFleet,
            takenHitsSquares,
            enemyFleet,
            hits,
            misses
        )
    }

    /**
     * Gets the number of registered games
     * @return the number of games
     */
    fun getNumberOfPlayedGames() = doService(data) { transaction ->
        data.gamesData.getNumberOfPlayedGames(transaction)
    }

    /**
     * Gets the game state of a game
     * @param gameId the id of the game
     * @return the state of the game
     */
    fun getGameState(gameId: Int) = doService(data) { transaction ->
        if (gameId <= 0) {
            throw AppException("Invalid gameId", AppExceptionStatus.BAD_REQUEST)
        }
        data.gamesData.getGameState(transaction, gameId)
            ?: throw AppException("Game does not exist", AppExceptionStatus.NOT_FOUND)
    }

    /**
     * Shows the state of the player's or enemy's fleet
     * @param userId the player's id
     * @param gameId the game's id
     * @param myFleet represents the fleet that is being requested
     * @return the state of every ship
     */
    fun fleetState(userId: Int, gameId: Int, myFleet: Boolean) = doService(data) { transaction ->
        val game = computeGame(transaction, gameId, data)
        checkPlayerInGame(game, userId)
        if (myFleet) {
            data.shipsData.getFleet(transaction, game.id, userId).map { it.toShipState() }
        } else {
            data.shipsData.getFleet(transaction, game.id, game.otherPlayer(userId)).map { it.toShipState() }
        }
    }

    /**
     * Sends the hits the user has thrown in his turn
     * @param userId the user's id
     * @param gameId the game's id
     * @param squares the list of all the squares hit by the user
     * @return every hit's outcome
     */
    fun sendHits(userId: Int, gameId: Int, squares: List<Square>) = doService(data) { transaction ->
        val game = computeGame(transaction, gameId, data)
        checkPlayerInGame(game, userId)
        checkCurrentPlayer(game, userId)
        checkGameState(game.state, "shooting")
        val gameType = game.type.toGameTypeOrNull()
            ?: throw AppException("Game type not registered")
        if (squares.isEmpty()) {
            throw AppException("No squares provided", AppExceptionStatus.BAD_REQUEST)
        }
        if (squares.size > gameType.shotsPerRound) {
            throw AppException("Invalid amount of hits", AppExceptionStatus.BAD_REQUEST)
        }
        val hitSquares = data.hitsData.getHits(transaction, game.id, game.idlePlayer())
            .map { it.square.toSquareOrThrow() }.toMutableSet()
        val verifiedSquares = mutableListOf<Square>()
        squares.forEach { square ->
            checkOrThrowBadRequest(!squareInBoard(square, gameType.boardSize), "Invalid square: ${square.getString()}")
            checkOrThrowBadRequest(hitSquares.contains(square), "Square already hit: ${square.getString()}")
            verifiedSquares.add(square)
        }
        val hitsOutcome = executeHit(transaction, game, squares, game.idlePlayer(), data)
        if (hitsOutcome.win) {
            data.gamesData.setGameStateCompleted(transaction, game.id)
            hitsOutcome
        } else {
            hitsOutcome
        }
    }

    /**
     * Sends a layout for a player in a game and
     * starts a game if both players have successfully defined their layout
     * @param userId the user's id
     * @param gameId the game's id
     * @param ships a list with all the information regarding the positioning of the ships
     * @return true if successful
     */
    fun sendLayout(userId: Int, gameId: Int, ships: List<ShipCreationInfo>) = doService(data) { transaction ->
        val game = computeGame(transaction, gameId, data)
        checkPlayerInGame(game, userId)
        checkGameState(game.state, "layout_definition")
        if (data.shipsData.hasShips(transaction, userId, game.id)) {
            throw AppException("Layout already defined", AppExceptionStatus.BAD_REQUEST)
        }
        val verifiedShips = checkShipLayout(userId, game, ships)
        data.shipsData.defineLayout(transaction, game.id, userId, verifiedShips)
        if (data.shipsData.checkEnemyLayoutDone(transaction, game.id, userId)) {
            println("enemy done")
            data.gamesData.setGameToShooting(transaction, game.id)
            LayoutOutcomeStatus.STARTED
        } else {
            LayoutOutcomeStatus.WAITING
        }
    }
}