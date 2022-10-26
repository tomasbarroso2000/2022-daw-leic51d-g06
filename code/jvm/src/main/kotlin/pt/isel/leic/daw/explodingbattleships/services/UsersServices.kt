package pt.isel.leic.daw.explodingbattleships.services

import org.springframework.stereotype.Component
import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.DataList
import pt.isel.leic.daw.explodingbattleships.domain.Ranking
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus
import pt.isel.leic.daw.explodingbattleships.services.utils.checkEmailValid
import pt.isel.leic.daw.explodingbattleships.services.utils.checkLimitAndSkip
import pt.isel.leic.daw.explodingbattleships.services.utils.checkPasswordValid
import pt.isel.leic.daw.explodingbattleships.services.utils.doService
import pt.isel.leic.daw.explodingbattleships.services.utils.enterLobbyOrCreateGame
import pt.isel.leic.daw.explodingbattleships.services.utils.isGameTypeInvalid

@Component
class UsersServices(private val data: Data) {

    /**
     * Creates a user
     * @param userInput the player information
     * @return the output of the player creation with the new player's id
     */
    fun createUser(name: String, email: String, password: String) = doService(data) { transaction ->
        if (name.isBlank()) {
            throw AppException("Invalid name", AppExceptionStatus.BAD_REQUEST)
        }
        if (email.isBlank()) {
            throw AppException("Invalid email", AppExceptionStatus.BAD_REQUEST)
        }
        if (password.isBlank()) {
            throw AppException("Invalid password", AppExceptionStatus.BAD_REQUEST)
        }
        checkEmailValid(email)
        checkPasswordValid(password)
        data.usersData.createUser(transaction, name, email, password.hashCode())
    }

    /**
     * Creates a token
     */
    fun createToken(email: String, password: String) = doService(data) { transaction ->
        val user = data.usersData.getUserFromEmail(transaction, email)
        if (user == null || user.passwordVer != password.hashCode()) {
            throw AppException("Bad credentials", AppExceptionStatus.UNAUTHORIZED)
        }
        data.usersData.createToken(transaction, user.id)
    }

    /**
     * Get the player with the token passed as parameter
     * @param token the user's token
     * @return the player
     */
    fun getPlayerInfo(token: String?) = doService(data) { transaction ->
        if (token.isNullOrBlank()) {
            throw AppException("No token provided", AppExceptionStatus.UNAUTHORIZED)
        }
        data.usersData.getUserFromToken(transaction, token)
            ?: throw AppException("Invalid token", AppExceptionStatus.UNAUTHORIZED)
    }

    /**
     * Gets the player rankings
     * @param limit the limit value of the list
     * @param skip the skip value of the list
     * @return a [DataList] with the players sorted by score
     */
    fun getRankings(limit: Int, skip: Int): DataList<Ranking> = doService(data) { transaction ->
        checkLimitAndSkip(limit, skip)
        data.usersData.getRankings(transaction, limit, skip)
    }

    /**
     * Places the user in a lobby or in a game if there is already someone waiting in the lobby with the same game characteristics
     * @param lobbyInput the characteristics of the game the user wants to play
     */
    fun enterLobby(userId: Int, gameType: String) = doService(data) { transaction ->
        if (isGameTypeInvalid(gameType)) {
            throw AppException("Invalid game type", AppExceptionStatus.BAD_REQUEST)
        }
        enterLobbyOrCreateGame(transaction, userId, gameType, data)
    }
}
