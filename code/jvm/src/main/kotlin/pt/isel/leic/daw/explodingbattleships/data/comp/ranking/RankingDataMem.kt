package pt.isel.leic.daw.explodingbattleships.data.comp.ranking

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.MockData
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.getSublist
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.hasMore
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.toPlayer
import pt.isel.leic.daw.explodingbattleships.domain.ListOfData
import pt.isel.leic.daw.explodingbattleships.domain.Player

class RankingDataMem(private val mockData: MockData) : RankingData {
    override fun getRankings(transaction: Transaction, limit: Int, skip: Int): ListOfData<Player> {
        val players = mockData.players.map { it.toPlayer() }.sortedBy { it.score }.reversed()
        return ListOfData(getSublist(players, limit, skip), hasMore(players.size, limit, skip))
    }
}
