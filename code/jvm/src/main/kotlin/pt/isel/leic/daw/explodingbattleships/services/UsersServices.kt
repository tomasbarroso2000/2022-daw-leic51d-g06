package pt.isel.leic.daw.explodingbattleships.services

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.*
import pt.isel.leic.daw.explodingbattleships.services.utils.*

class UsersServices(private val data: Data) {

    /**
     * Creates a user
     * @param userInput the player information
     * @return the output of the player creation with the new player's id
     */
    fun createUser(userInput: UserInput) = doService(data) {transaction ->
        if (userInput.name.isNullOrBlank())
            throw AppException("Invalid name", AppExceptionStatus.BAD_REQUEST)
        if (userInput.email.isNullOrBlank())
            throw AppException("Invalid email", AppExceptionStatus.BAD_REQUEST)
        if (userInput.password.isNullOrBlank())
            throw AppException("Invalid password", AppExceptionStatus.BAD_REQUEST)
        checkPasswordValid(userInput.password)
        data.usersData.createUser(transaction, userInput.name, userInput.email, userInput.password.hashCode())
    }

    /**
     * Creates a token
     */
    fun createToken(email: String, password: String) = doService(data) { transaction ->
        if (email.isNullOrBlank())
            throw AppException("Invalid email", AppExceptionStatus.BAD_REQUEST)
        if (password.isNullOrBlank())
            throw AppException("Invalid password", AppExceptionStatus.BAD_REQUEST)
        //data.usersData.createToken(transaction, )
    }

    /**
     * Get the player with the token passed as parameter
     * @param token the user's token
     * @return the player
     */
    fun getPlayerInfo(token: String?) = doService(data) { transaction ->
        computePlayer(transaction, token, data)
    }

    /**
     * Gets the player rankings
     * @param limit the limit value of the list
     * @param skip the skip value of the list
     * @return a [ListOfData] with the players sorted by score
     */
    fun getRankings(limit: Int, skip: Int): Rankings = doService(data) { transaction ->
        checkLimitAndSkip(limit, skip)
        data.usersData.getRankings(transaction, limit, skip)
    }

    /**
     * Places the user in a lobby or in a game if there is already someone waiting in the lobby with the same game characteristics
     * @param lobbyInput the characteristics of the game the user wants to play
     * @return a [EnterLobbyOutput] representing if the player was placed in queue
     */
    fun enterLobby(player: User, lobbyInput: EnterLobbyInput) = doService(data) { transaction ->
        if (lobbyInput.gameType == null || isGameTypeInvalid(lobbyInput.gameType))
            throw AppException("Invalid game type", AppExceptionStatus.BAD_REQUEST)
        enterLobbyOrCreateGame(transaction, player.id, lobbyInput.gameType, data)
    }
}