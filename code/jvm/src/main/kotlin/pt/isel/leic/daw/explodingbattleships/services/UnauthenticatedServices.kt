package pt.isel.leic.daw.explodingbattleships.services

import org.springframework.stereotype.Component
import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.*
import pt.isel.leic.daw.explodingbattleships.services.utils.*

/**
 * Section of services that don't require authentication
 */
@Component
class UnauthenticatedServices(private val data: Data) {
    /**
     * Creates a player
     * @param playerInput the player information
     * @return the output of the player creation with the new player's id
     */
    fun createPlayer(playerInput: PlayerInput) = doService(data) {
        if (playerInput.name.isNullOrBlank())
            throw AppException("Invalid name", AppExceptionStatus.BAD_REQUEST)
        if (playerInput.email.isNullOrBlank())
            throw AppException("Invalid email", AppExceptionStatus.BAD_REQUEST)
        if (playerInput.password.isNullOrBlank())
            throw AppException("Invalid password", AppExceptionStatus.BAD_REQUEST)
        checkPasswordValid(playerInput.password)
        data.playersData.createPlayer(it, playerInput.name, playerInput.email, playerInput.password.hashCode())
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
     * Gets the player rankings
     * @param limit the limit value of the list
     * @param skip the skip value of the list
     * @return a [ListOfData] with the players sorted by score
     */
    fun getRankings(limit: Int, skip: Int): Rankings = doService(data) { transaction ->
        checkLimitAndSkip(limit, skip)
        data.playersData.getRankings(transaction, limit, skip)
    }

    /**
     * Gets the system information
     * @return the system information
     */
    fun getSystemInfo() = SystemInfo()
}
