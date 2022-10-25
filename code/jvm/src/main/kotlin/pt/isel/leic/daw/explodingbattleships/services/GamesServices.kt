package pt.isel.leic.daw.explodingbattleships.services

import org.springframework.stereotype.Component
import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.*
import pt.isel.leic.daw.explodingbattleships.services.utils.*

@Component
class GamesServices(private val data: Data) {

    fun getGame(userId: Int, gameId: Int) = doService(data) { transaction ->
        val game = computeGame(transaction, gameId, data)
        val opponentId = game.otherPlayer(userId)
        val playerFleet = data.shipsData.getFleet(transaction, game.id, userId)
        val takenHits = data.hitsData.getHits(transaction, game.id, userId)
        val enemyFleet = data.shipsData.getFleet(transaction, game.id, opponentId)
        val sentHits = data.hitsData.getHits(transaction, game.id, opponentId)
        FullGameInfo(game, playerFleet, takenHits, enemyFleet, sentHits)
    }

    /**
     * Gets the number of games registered
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
    fun getGameState(gameId: Int?) = doService(data) { transaction ->
        if (gameId == null || gameId <= 0)
            throw AppException("Invalid gameId", AppExceptionStatus.BAD_REQUEST)
        data.gamesData.getGameState(transaction, gameId)
            ?: throw AppException("Game does not exist", AppExceptionStatus.NOT_FOUND)
    }

    /**
     * Shows the state of the player's or enemy's fleet
     * @param player the player
     * @param fleet represents the fleet that is being requested
     * @return the state of every ship
     */
    fun fleetState(userId: Int, gameId: Int?, myFleet: Boolean?) = doService(data) { transaction ->
        val game = computeGame(transaction, gameId, data)
        checkPlayerInGame(game, userId)
        if (myFleet == null)
            throw AppException("Fleet not specified", AppExceptionStatus.BAD_REQUEST)
        if (myFleet)
            data.shipsData.fleetState(transaction, game.id, userId)
        else
            data.shipsData.fleetState(transaction, game.id, game.otherPlayer(userId))
    }

    /**
     * Sends the hits the user has thrown in his turn
     * @param player the player
     * @param hits the hits to be sent
     * @return every hit's outcome
     */
    fun sendHits(userId: Int, gameId: Int?, squares: List<UnverifiedSquare>?) = doService(data) { transaction ->
        val game = computeGame(transaction, gameId, data)
        checkPlayerInGame(game, userId)
        checkCurrentPlayer(game, userId)
        checkGameState(game.state, "shooting")
        val gameType = game.type.toGameType()
            ?: throw AppException("Game type not registered")
        if (squares.isNullOrEmpty())
            throw AppException("No squares provided", AppExceptionStatus.BAD_REQUEST)
        if (squares.size > gameType.shotsPerRound)
            throw AppException("Invalid amount of hits", AppExceptionStatus.BAD_REQUEST)
        val hitSquares = data.gamesData.getHitSquares(transaction, game.id, game.idlePlayer()).toMutableSet()
        val verifiedSquares = mutableListOf<VerifiedSquare>()
        squares.forEach { unverifiedSquare ->
            val verifiedSquare = unverifiedSquare.toVerifiedSquareOrNull()
                ?: throw AppException("Invalid square: ${unverifiedSquare.getString()}", AppExceptionStatus.BAD_REQUEST)
            checkOrThrowBadRequest(!squareInBoard(verifiedSquare, gameType.boardSize), "Invalid square: ${unverifiedSquare.getString()}")
            checkOrThrowBadRequest(hitSquares.contains(verifiedSquare), "Square already hit: ${unverifiedSquare.getString()}")
            verifiedSquares.add(verifiedSquare)
        }
        val hitsOutcome = executeHit(transaction, game, verifiedSquares, game.idlePlayer(), data)
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
     * @param player the player
     * @param layout the player's chosen layout
     * @return true if successful
     */
    fun sendLayout(userId: Int, gameId: Int?, ships: List<UnverifiedShip>?) = doService(data) { transaction ->
        val game = computeGame(transaction, gameId, data)
        checkPlayerInGame(game, userId)
        checkGameState(game.state, "layout_definition")
        if (ships == null)
            throw AppException("No ships provided", AppExceptionStatus.BAD_REQUEST)
        if (data.shipsData.hasShips(transaction, userId, game.id))
            throw AppException("Layout already defined", AppExceptionStatus.BAD_REQUEST)
        val verifiedShips = checkShipLayout(game.type, ships)
        data.shipsData.defineLayout(transaction, game.id, userId, verifiedShips)
        if (data.shipsData.checkEnemyLayoutDone(transaction, game.id, userId)) {
            println("enemy done")
            data.gamesData.setGameToShooting(transaction, game.id)
            LayoutOutcomeStatus.STARTED
        }
        else
            LayoutOutcomeStatus.WAITING
    }
}