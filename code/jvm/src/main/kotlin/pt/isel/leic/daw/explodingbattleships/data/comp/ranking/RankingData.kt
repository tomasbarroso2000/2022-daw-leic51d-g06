package pt.isel.leic.daw.explodingbattleships.data.comp.ranking

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.ListOfData
import pt.isel.leic.daw.explodingbattleships.domain.Player

interface RankingData {
    fun getRankings(transaction: Transaction, limit: Int, skip: Int): ListOfData<Player>
}
