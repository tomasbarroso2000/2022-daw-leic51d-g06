package pt.isel.leic.daw.explodingbattleships.data

/**
 * Represents a transaction that can be requested
 * by the services module to execute sequential tasks
 * on the data without using multiple connections
 */
interface Transaction {
    /**
     * Begins a transaction
     */
    fun begin()

    /**
     * Commits a transaction
     */
    fun commit()

    /**
     * Rollbacks a transaction
     */
    fun rollback()

    /**
     * Closes a connection
     */
    fun close()
}