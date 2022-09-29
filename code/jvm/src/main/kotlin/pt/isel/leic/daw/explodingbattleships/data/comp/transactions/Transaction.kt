package pt.isel.leic.daw.explodingbattleships.data.comp.transactions

/**
 * Represents a transaction that can be requested
 * by the services module to execute sequential tasks
 * on the data without using multiple connections
 */
interface Transaction {
    /**
     * Begin a transaction
     */
    fun begin()

    /**
     * Commit a transaction
     */
    fun commit()

    /**
     * Rollback a transaction
     */
    fun rollback()

    /**
     * Close a connection
     */
    fun close()
}
