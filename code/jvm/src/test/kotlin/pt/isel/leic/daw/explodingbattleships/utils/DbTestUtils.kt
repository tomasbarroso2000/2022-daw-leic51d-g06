package pt.isel.leic.daw.explodingbattleships.utils

import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.data.db.DataDb

/**
 * Executes a test within a [Transaction] and does a rollback
 * @param function the code to be executed
 * @return the value returned by function
 */
fun testAndRollback(function: (Transaction, DataDb) -> Unit) {
    val db = DataDb()
    val transaction = db.getTransaction()
    try {
        transaction.begin()
        function(transaction, db)
    } finally {
        transaction.rollback()
        transaction.close()
    }
}