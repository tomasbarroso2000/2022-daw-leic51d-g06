package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.data.UsersData
import pt.isel.leic.daw.explodingbattleships.domain.DataList
import pt.isel.leic.daw.explodingbattleships.domain.Ranking
import pt.isel.leic.daw.explodingbattleships.domain.User
import java.util.UUID

class UsersDataDb : UsersData {
    override fun getUserFromToken(transaction: Transaction, tokenVer: String): User? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.select(
                "select * from tokens" +
                    " join users on user_id = id where token_ver = :tokenVer"
            )
                .bind("tokenVer", tokenVer)
                .mapTo<User>().firstOrNull()
        }

    override fun getUserFromEmail(transaction: Transaction, email: String): User? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.select("select * from users where email = :email")
                .bind("email", email)
                .mapTo<User>().firstOrNull()
        }

    override fun createUser(transaction: Transaction, name: String, email: String, passwordVer: String): Int =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "insert into users (name, email, score, password_ver)" +
                    " values (:name, :email, 0, :passwordVer)"
            )
                .bind("name", name)
                .bind("email", email)
                .bind("passwordVer", passwordVer)
                .executeAndReturnGeneratedKeys()
                .mapTo<Int>()
                .first()
        }

    override fun createToken(transaction: Transaction, userId: Int, tokenVer: String) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("insert into tokens values (:tokenVer, :userId)")
                .bind("tokenVer", tokenVer)
                .bind("userId", userId)
                .execute()
        }
    }

    override fun getRankings(transaction: Transaction, limit: Int, skip: Int): DataList<Ranking> =
        (transaction as TransactionDataDb).withHandle { handle ->
            val foundRankings =
                handle.createQuery("select id, name, score from users order by score desc offset :skip limit :limit")
                    .bind("skip", skip)
                    .bind("limit", limit + 1)
                    .mapTo<Ranking>().list()
            val rankings = mutableListOf<Ranking>()
            val hasMore = getHasMoreAndProcessList(foundRankings, rankings, limit)
            DataList(rankings, hasMore)
        }
}