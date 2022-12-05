package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.data.UsersData
import pt.isel.leic.daw.explodingbattleships.domain.DataList
import pt.isel.leic.daw.explodingbattleships.domain.User
import pt.isel.leic.daw.explodingbattleships.domain.UserInfo

class UsersDataDb : UsersData {

    override fun getUserById(transaction: Transaction, userId: Int): User? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from users where id = :userId")
                .bind("userId", userId)
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

    override fun getRankings(transaction: Transaction, limit: Int, skip: Int): DataList<UserInfo> =
        (transaction as TransactionDataDb).withHandle { handle ->
            val foundRankings =
                handle.createQuery("select id, name, score from users order by score desc offset :skip limit :limit")
                    .bind("skip", skip)
                    .bind("limit", limit + 1)
                    .mapTo<UserInfo>().list()
            val rankings = mutableListOf<UserInfo>()
            val hasMore = getHasMoreAndProcessList(foundRankings, rankings, limit)
            DataList(rankings, hasMore)
        }

    override fun increasePlayerScore(transaction: Transaction, userId: Int) {
        val oldScore: Int = (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select score from user where id = :id")
                .bind("id", userId)
                .mapTo<Int>().first()
        }
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate("update users set score = :score where id = :userId")
                .bind("score", oldScore + 10)
                .bind("userId", userId)
                .execute()
        }
    }
}