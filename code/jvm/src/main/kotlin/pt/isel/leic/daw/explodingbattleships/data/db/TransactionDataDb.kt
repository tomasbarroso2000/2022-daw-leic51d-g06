package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import pt.isel.leic.daw.explodingbattleships.data.Transaction

class TransactionDataDb(private val handle: Handle) : Transaction {

    init {
        handle.setTransactionIsolation(TransactionIsolationLevel.SERIALIZABLE)
    }

    /**
     * Executes a task using the transaction's connection
     * @param function the task that will be executed
     */
    fun <T> withHandle(function: (h: Handle) -> T) = function(handle)

    override fun begin() { handle.begin() }
    override fun commit() { handle.commit() }
    override fun rollback() { handle.rollback() }
    override fun close() { handle.close() }
}
