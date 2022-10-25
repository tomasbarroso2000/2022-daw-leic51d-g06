package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import pt.isel.leic.daw.explodingbattleships.data.Data

@Component
class DataDb : Data {

    /**
     * Jdbi object useful for transaction management
     */
    private val jdbi = getJdbi()

    /**
     * Obtains a [Jdbi] object
     */
    private fun getJdbi(): Jdbi {
        val dataSource = PGSimpleDataSource()
        val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")
        dataSource.setURL(jdbcDatabaseURL)
        return Jdbi.create(dataSource).installPlugin(KotlinPlugin())
    }

    override val usersData = UsersDataDb()
    override val gamesData = GamesDataDb()
    override val lobbiesData = LobbiesDataDb()
    override val shipsData = ShipsDataDb()
    override val hitsData = HitsDataDb()

    override fun getTransaction() = TransactionDataDb(jdbi.open())
}
