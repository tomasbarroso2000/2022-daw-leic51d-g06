package pt.isel.leic.daw.explodingbattleships.services

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.DataList
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutcome
import pt.isel.leic.daw.explodingbattleships.domain.Ranking
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus
import pt.isel.leic.daw.explodingbattleships.services.utils.checkEmailValid
import pt.isel.leic.daw.explodingbattleships.services.utils.checkLimitAndSkip
import pt.isel.leic.daw.explodingbattleships.services.utils.checkOrThrowBadRequest
import pt.isel.leic.daw.explodingbattleships.services.utils.checkPasswordValid
import pt.isel.leic.daw.explodingbattleships.services.utils.doService
import pt.isel.leic.daw.explodingbattleships.services.utils.getGameType
import pt.isel.leic.daw.explodingbattleships.utils.TokenEncoder
import java.security.SecureRandom
import java.util.*

@Component
class UsersServices(
    private val data: Data,
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder
) {

    companion object {
        private const val TOKEN_BYTE_SIZE = 256 / 8

        private fun generateToken() =
            ByteArray(TOKEN_BYTE_SIZE).let { byteArray ->
                SecureRandom.getInstanceStrong().nextBytes(byteArray)
                Base64.getUrlEncoder().encodeToString(byteArray)
            }
    }

    /**
     * Creates a user
     * @param name the user's name
     * @param email the user's email
     * @param password the user's password
     * @return the new user id
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
        data.usersData.createUser(transaction, name, email, passwordEncoder.encode(password))
    }

    /**
     * Creates a token
     * @param email the user's email
     * @param password the user's password
     * @return the user's token
     */
    fun createToken(email: String, password: String): String = doService(data) { transaction ->
        val user = data.usersData.getUserFromEmail(transaction, email)
        if (user == null || !passwordEncoder.matches(password, user.passwordVer)) {
            throw AppException("Bad credentials", AppExceptionStatus.UNAUTHORIZED)
        }
        val token = generateToken()
        val tokenVer = tokenEncoder.hash(token)
        data.usersData.createToken(transaction, user.id, tokenVer)
        token
    }

    /**
     * Gets the user with the corresponding token
     * @param token the user's token
     * @return the user
     */
    fun getPlayerInfo(token: String?) = doService(data) { transaction ->
        if (token.isNullOrBlank()) {
            throw AppException("No token provided", AppExceptionStatus.UNAUTHORIZED)
        }
        val tokenVer = tokenEncoder.hash(token)
        data.usersData.getUserFromToken(transaction, tokenVer)
            ?: throw AppException("Invalid token", AppExceptionStatus.UNAUTHORIZED)
    }

    /**
     * Gets the player rankings
     * @param limit the limit value of the list
     * @param skip the skip value of the list
     * @return a list with the players sorted by score
     */
    fun getRankings(limit: Int, skip: Int): DataList<Ranking> = doService(data) { transaction ->
        checkLimitAndSkip(limit, skip)
        data.usersData.getRankings(transaction, limit, skip)
    }

    /**
     * Places the user in a lobby or in a game if there is already other player
     * waiting in a lobby with the same game characteristics
     * @param userId the user id
     * @param gameTypeName the name of the game type the user wants to play
     * @return the enter-lobby status
     */
    fun enterLobby(userId: Int, gameTypeName: String) = doService(data) { transaction ->
        val gameType = getGameType(transaction, gameTypeName, data)
        val matchingLobby = data.lobbiesData.searchLobbies(transaction, gameType.name, userId).firstOrNull()
        if (matchingLobby != null) {
            val gameId = data.gamesData.createGame(transaction, gameType.name, userId, matchingLobby.userId)
            data.lobbiesData.setGameId(transaction, matchingLobby.id, gameId)
            EnterLobbyOutcome(false, gameId)
        } else {
            val lobbyId = data.lobbiesData.enterLobby(transaction, userId, gameType.name)
            EnterLobbyOutcome(true, lobbyId)
        }
    }

    /**
     * Checks if a game was created for a lobby
     * @param userId the user id
     * @param lobbyId the lobby id
     * @return the created game id or null
     */
    fun enteredGame(userId: Int, lobbyId: Int) = doService(data) { transaction ->
        checkOrThrowBadRequest(lobbyId <= 0, "Invalid lobby id")
        val lobby = data.lobbiesData.getLobbyById(transaction, lobbyId)
            ?: throw AppException("Lobby doesn't exist", AppExceptionStatus.NOT_FOUND)
        if (lobby.userId != userId) {
            throw AppException("User not in lobby", AppExceptionStatus.UNAUTHORIZED)
        }
        if (lobby.gameId != null) {
            data.lobbiesData.removeLobby(transaction, lobbyId)
        }
        lobby.gameId
    }
}