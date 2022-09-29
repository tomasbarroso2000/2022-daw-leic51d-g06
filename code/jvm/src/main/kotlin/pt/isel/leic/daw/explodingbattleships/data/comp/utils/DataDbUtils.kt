package pt.isel.leic.daw.explodingbattleships.data.comp.utils

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.postgresql.ds.PGSimpleDataSource

/**
 * Initialize the connection to the data source
 */
fun connectionInit(): Handle {
    val dataSource = PGSimpleDataSource()
    val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")
    dataSource.setURL(jdbcDatabaseURL)
    return Jdbi.create(dataSource).installPlugin(KotlinPlugin()).open() // installing plugin every time init runs
}
