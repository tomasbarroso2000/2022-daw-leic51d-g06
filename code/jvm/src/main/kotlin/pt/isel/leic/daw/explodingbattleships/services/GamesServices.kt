package pt.isel.leic.daw.explodingbattleships.services

import org.springframework.stereotype.Component
import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.AvailableGame
import pt.isel.leic.daw.explodingbattleships.domain.DataList
import pt.isel.leic.daw.explodingbattleships.domain.FullGameInfo
import pt.isel.leic.daw.explodingbattleships.domain.LayoutOutcomeStatus
import pt.isel.leic.daw.explodingbattleships.domain.ShipCreationInfo
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.domain.toSquare
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus
import pt.isel.leic.daw.explodingbattleships.services.utils.checkCurrentPlayer
import pt.isel.leic.daw.explodingbattleships.services.utils.checkGameState
import pt.isel.leic.daw.explodingbattleships.services.utils.checkLimitAndSkip
import pt.isel.leic.daw.explodingbattleships.services.utils.checkOrThrowBadRequest
import pt.isel.leic.daw.explodingbattleships.services.utils.checkPlayerInGame
import pt.isel.leic.daw.explodingbattleships.services.utils.checkShipLayout
import pt.isel.leic.daw.explodingbattleships.services.utils.computeGame
import pt.isel.leic.daw.explodingbattleships.services.utils.doService
import pt.isel.leic.daw.explodingbattleships.services.utils.executeHit
import pt.isel.leic.daw.explodingbattleships.services.utils.getGameOrThrow
import pt.isel.leic.daw.explodingbattleships.services.utils.getGameType
import pt.isel.leic.daw.explodingbattleships.services.utils.squareInBoard

@Component
class GamesServices(private val data: Data) {

    /**
     * Gets all the games the user is currently playing
     * @param userId the user id
     */
    fun getCurrentlyPlayingGames(userId: Int, limit: Int, skip: Int) = doService(data) { transaction ->
        checkLimitAndSkip(limit, skip)
        val userPlayingGames = mutableListOf<AvailableGame>()
        val userGames = data.gamesData.getGames(transaction, userId, limit, skip)
        for (game in userGames.list) {
            if (game.state != "completed")
                userPlayingGames.add(
                    AvailableGame(
                        game.id,
                        game.type,
                        game.state,
                        if (game.player1 == userId)
                            game.player2
                        else
                            game.player1
                    )
                )
        }
        DataList(userPlayingGames, userGames.hasMore)
    }

    /**
     * Gets all the information of a game
     * @param userId the id of the user
     * @param gameId the id of the game
     * @return all the information of the game
     */
    fun getGame(userId: Int, gameId: Int) = doService(data) { transaction ->
        val game = computeGame(transaction, gameId, data)
        val opponentId = game.otherPlayer(userId)
        val playing = game.currPlayer == userId
        val playerFleet = data.shipsData.getFleet(transaction, game.id, userId)
        val takenHitsSquares = data.hitsData.getHits(transaction, game.id, userId).map { it.square.toSquare() }
        val enemyFleet = data.shipsData.getFleet(transaction, game.id, opponentId).filter { it.destroyed }
        val sentHits = data.hitsData.getHits(transaction, game.id, opponentId)
        val hits = sentHits.filter { it.onShip }.map { it.square.toSquare() }
        val misses = sentHits.filter { !it.onShip }.map { it.square.toSquare() }
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
     * Gets the total number of played games
     * @return the number of played games
     */
    fun getNumberOfPlayedGames() = doService(data) { transaction ->
        data.gamesData.getNumberOfPlayedGames(transaction)
    }

    /**
     * Gets the state of a game
     * @param gameId the game id
     * @return the game state
     */
    fun getGameState(gameId: Int) = doService(data) { transaction ->
        computeGame(transaction, gameId, data).state
    }

    /**
     * Shows the state of the player's or enemy's fleet
     * @param userId the player id
     * @param gameId the game id
     * @param myFleet if it is the player fleet
     * @return a list with the state of every ship
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
     * @param userId the user id
     * @param gameId the game id
     * @param squares the list of all the squares hit
     * @return a list with all the hit's outcomes
     */
    fun sendHits(userId: Int, gameId: Int, squares: List<Square>) = doService(data) { transaction ->
        val game = computeGame(transaction, gameId, data)
        checkPlayerInGame(game, userId)
        checkGameState(game.state, "shooting")
        checkCurrentPlayer(game, userId)
        val gameType = getGameType(transaction, game.type, data)
        if (squares.isEmpty()) {
            throw AppException("Empty squares", "No squares provided", AppExceptionStatus.BAD_REQUEST)
        }
        if (squares.size > gameType.shotsPerRound) {
            throw AppException(
                "Invalid amount of hits",
                "Can't send more than ${gameType.shotsPerRound} shots",
                AppExceptionStatus.BAD_REQUEST
            )
        }
        val hitSquares = data.hitsData.getHits(transaction, game.id, game.idlePlayer())
            .map { it.square.toSquare() }.toMutableSet()
        val verifiedSquares = mutableListOf<Square>()
        squares.forEach { square ->
            checkOrThrowBadRequest(
                !squareInBoard(square, gameType.boardSize),
                "Invalid square",
                "Square not in board: $square"
            )
            checkOrThrowBadRequest(
                hitSquares.contains(square),
                "Invalid square",
                "Square already hit: $square"
            )
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
     * @param userId the user id
     * @param gameId the game id
     * @param ships a list with all the information regarding the positioning of the ships
     * @return the layout outcome status
     */
    fun sendLayout(userId: Int, gameId: Int, ships: List<ShipCreationInfo>) = doService(data) { transaction ->
        val game = getGameOrThrow(transaction, gameId, data)
        checkPlayerInGame(game, userId)
        checkGameState(game.state, "layout_definition")
        if (data.shipsData.hasShips(transaction, userId, game.id)) {
            throw AppException(
                "Layout already defined",
                "Your layout has already been defined",
                AppExceptionStatus.BAD_REQUEST
            )
        }
        val layout = checkShipLayout(transaction, userId, game, ships, data)
        data.shipsData.defineLayout(transaction, game.id, userId, layout)
        if (data.shipsData.checkEnemyLayoutDone(transaction, game.id, userId)) {
            data.gamesData.setGameToShooting(transaction, game.id)
            LayoutOutcomeStatus.STARTED
        } else {
            LayoutOutcomeStatus.WAITING
        }
    }
}