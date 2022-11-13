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
import pt.isel.leic.daw.explodingbattleships.services.utils.isTokenStillValid
import pt.isel.leic.daw.explodingbattleships.utils.TokenEncoder
import java.security.SecureRandom
import java.time.Duration
import java.util.*

@Component
class UsersServices(
    private val data: Data,
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder
) {

    companion object {
        val TOKEN_ROLLING_TTL: Duration = Duration.ofHours(1)
        val TOKEN_TTL: Duration = Duration.ofDays(1)
        private const val MAX_TOKENS = 3
        private const val TOKEN_BYTE_SIZE = 256 / 8

        /**
         * Generates a token
         * @return the generated token
         */
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
            throw AppException("Invalid name", "Name is empty", AppExceptionStatus.BAD_REQUEST)
        }
        if (email.isBlank()) {
            throw AppException("Invalid email", "Email is empty", AppExceptionStatus.BAD_REQUEST)
        }
        if (password.isBlank()) {
            throw AppException("Invalid password", "Password is empty", AppExceptionStatus.BAD_REQUEST)
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
            throw AppException("Bad credentials", "Credentials don't match any user", AppExceptionStatus.UNAUTHORIZED)
        }
        val token = generateToken()
        val tokenVer = tokenEncoder.hash(token)
        data.tokensData.createToken(transaction, user.id, tokenVer, MAX_TOKENS)
        token
    }

    /**
     * Gets the user with the corresponding token
     * @param token the user's token
     * @return the user
     */
    fun getPlayerInfo(token: String?) = doService(data) { transaction ->
        if (token.isNullOrBlank()) {
            throw AppException("Invalid token", "No token provided", AppExceptionStatus.UNAUTHORIZED)
        }
        val tokenVer = tokenEncoder.hash(token)
        val actualToken = data.tokensData.getToken(transaction, tokenVer)
        if (actualToken == null || !isTokenStillValid(actualToken)) {
            throw AppException("Invalid token", "Token cannot be used", AppExceptionStatus.UNAUTHORIZED)
        }
        data.tokensData.updateTokenLastUsed(transaction, actualToken.tokenVer)
        data.usersData.getUserById(transaction, actualToken.userId)
            ?: throw IllegalArgumentException("User not found")
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
        checkOrThrowBadRequest(
            lobbyId <= 0,
            "Invalid lobby id",
            "Lobby id must be greater than 0"
        )
        val lobby = data.lobbiesData.getLobbyById(transaction, lobbyId)
            ?: throw AppException("Lobby not found", "Lobby doesn't exist", AppExceptionStatus.NOT_FOUND)
        if (lobby.userId != userId) {
            throw AppException("Unauthorized lobby", "Not in that lobby", AppExceptionStatus.UNAUTHORIZED)
        }
        if (lobby.gameId != null) {
            data.lobbiesData.removeLobby(transaction, lobbyId)
        }
        lobby.gameId
    }
}