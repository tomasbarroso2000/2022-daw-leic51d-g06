package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.UsersData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.*
import java.util.UUID

class UsersDataDb : UsersData {
    override fun getUserFromToken(transaction: Transaction, token: String): User? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.select("select id, name, email, score, password_ver from tokens join users on user_id = id where token_ver = :token")
                .bind("token", token)
                .mapTo<User>().firstOrNull()
        }

    override fun getUserFromEmail(transaction: Transaction, email: String): User? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.select("select * from users where email = :email")
                .bind("email", email)
                .mapTo<User>().firstOrNull()
        }

    override fun createUser(transaction: Transaction, name: String, email: String, password: Int): UserOutput =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("insert into users (name, email, score, password_ver) values (:name, :email, 0, :password)")
                .bind("name", name)
                .bind("email", email)
                .bind("password", password)
                .executeAndReturnGeneratedKeys()
                .mapTo<Int>()
                .first().let { UserOutput(it) }
        }

    override fun createToken(transaction: Transaction, userId: Int): String =
        (transaction as TransactionDataDb).withHandle { handle ->
            val token = UUID.randomUUID().toString()
            handle.createUpdate("insert into tokens values (:token, :userId)")
                .bind("token", token)
                .bind("userId", userId)
                .execute()
            token
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

