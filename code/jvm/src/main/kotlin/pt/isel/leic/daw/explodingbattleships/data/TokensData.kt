package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.Token

interface TokensData {

    /**
     * Gets a token
     * @param transaction the current transaction
     * @param token the token string
     * @return the [Token]
     */
    fun getToken(transaction: Transaction, token: String): Token?

    /**
     * Creates a token for a user
     * @param transaction the current transaction
     * @param userId the user id
     * @param tokenVer the encoded token
     * @param maxTokens the maximum number of tokens a user can have
     */
    fun createToken(transaction: Transaction, userId: Int, tokenVer: String, maxTokens: Int)

    /**
     * Updates a last used timestamp of a token
     * @param transaction the current transaction
     * @param tokenVer the token
     */
    fun updateTokenLastUsed(transaction: Transaction, tokenVer: String)
}