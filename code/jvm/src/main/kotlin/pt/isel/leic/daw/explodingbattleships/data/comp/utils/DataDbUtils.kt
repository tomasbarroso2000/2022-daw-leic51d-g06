package pt.isel.leic.daw.explodingbattleships.data.comp.utils

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.postgresql.ds.PGSimpleDataSource

val jdbiObject = getJdbi()

/**
 * Creates a new Jdbi object with the Kotlin plugin
 */
fun getJdbi(): Jdbi {
    val dataSource = PGSimpleDataSource()
    val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")
    dataSource.setURL(jdbcDatabaseURL)
    return Jdbi.create(dataSource).installPlugin(KotlinPlugin())
}
