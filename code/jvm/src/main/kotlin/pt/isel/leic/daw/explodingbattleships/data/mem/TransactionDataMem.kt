package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.Transaction

class TransactionDataMem : Transaction {
    override fun begin() { }
    override fun commit() { }
    override fun rollback() { }
    override fun close() { }
}