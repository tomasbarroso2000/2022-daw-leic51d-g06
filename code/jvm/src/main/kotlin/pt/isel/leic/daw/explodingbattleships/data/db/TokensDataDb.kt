package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.TokensData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Token

class TokensDataDb : TokensData {

    override fun getToken(transaction: Transaction, token: String): Token? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.select("select * from tokens where token_ver = :token")
                .bind("token", token)
                .mapTo<Token>().firstOrNull()
        }

    override fun createToken(transaction: Transaction, userId: Int, tokenVer: String, maxTokens: Int) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "delete from tokens " +
                    "where user_id = :user_id and token_ver in " +
                    "(select token_ver from tokens where user_id = :user_id " +
                    "order by last_used_at desc offset :offset)"
            )
                .bind("user_id", userId)
                .bind("offset", maxTokens - 1)
                .execute()
            handle.createUpdate("insert into tokens values (:tokenVer, :userId, now(), now())")
                .bind("tokenVer", tokenVer)
                .bind("userId", userId)
                .execute()
        }
    }

    override fun updateTokenLastUsed(transaction: Transaction, tokenVer: String) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("update tokens set last_used_at = now() where token_ver = :tokenVer")
                .bind("tokenVer", tokenVer)
                .execute()
        }
    }
}