package pt.isel.leic.daw.explodingbattleships.data.comp.transactions

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.transaction.TransactionIsolationLevel

class TransactionDataDb(private val handle: Handle) : Transaction {

    init {
        handle.setTransactionIsolation(TransactionIsolationLevel.SERIALIZABLE)
    }

    /**
     * Execute a task using the transaction's connection
     * @param function the task that will be executed
     */
    fun withHandle(function: (h: Handle) -> Unit) = function(handle)

    override fun begin() { handle.begin() }
    override fun commit() { handle.commit() }
    override fun rollback() { handle.rollback() }
    override fun close() { handle.close() }

}
