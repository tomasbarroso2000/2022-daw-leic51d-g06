package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.postgresql.ds.PGSimpleDataSource
import java.sql.Timestamp
import java.time.Instant

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

/**
 * Processes the list received from the database and returns true if there were more items in the result set
 * @param receivedList the list received from the database
 * @param finalList the final to be return to the calling function
 * @param limit the maximum size of the final list
 * @return a [Boolean] if there were more items in the result set
 */
fun <T> getHasMoreAndProcessList(receivedList: List<T>, finalList: MutableList<T>, limit: Int): Boolean {
    var found = 0
    receivedList.forEach {
        found++
        if (found <= limit)
            finalList.add(it)
        else
            return true
    }
    return false
}

fun Timestamp.toInstant(): Instant = Instant.ofEpochSecond(time)

fun Instant.toTimestamp(): Timestamp = Timestamp.from(this)

/*fun Interval.toInstant(): Instant {

}*/
