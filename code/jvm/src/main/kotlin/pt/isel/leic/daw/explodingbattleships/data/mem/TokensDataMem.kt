package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.TokensData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Token
import java.time.Instant

class TokensDataMem(private val mockData: MockData) : TokensData {

    override fun getToken(transaction: Transaction, token: String): Token? =
        mockData.tokens.find { it.tokenVer == token }

    override fun createToken(transaction: Transaction, userId: Int, tokenVer: String, maxTokens: Int) {
        val tokensToKeep =
            mockData.tokens
                .filter { it.userId == userId }
                .sortedBy { it.lastUsedAt }
                .reversed()
                .filterIndexed { index, _ -> index < maxTokens - 1 }
        mockData.tokens.removeIf { it.userId == userId && !tokensToKeep.contains(it) }
        mockData.tokens.add(Token(tokenVer, userId, Instant.now(), Instant.now()))
    }

    override fun updateTokenLastUsed(transaction: Transaction, tokenVer: String) {
        mockData.tokens.find { it.tokenVer == tokenVer }?.let { token ->
            mockData.tokens.remove(token)
            val newToken = token.copy(lastUsedAt = Instant.now())
            mockData.tokens.add(newToken)
        }
    }
}