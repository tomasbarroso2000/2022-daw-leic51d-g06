package pt.isel.leic.daw.explodingbattleships.data.comp.ranking

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.TransactionDataDb
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.toPlayer
import pt.isel.leic.daw.explodingbattleships.domain.ListOfData
import pt.isel.leic.daw.explodingbattleships.domain.Player

class RankingDataDb : RankingData {
    override fun getRankings(transaction: Transaction, limit: Int, skip: Int): ListOfData<Player> {
        val players = mutableListOf<Player>()
        var hasMore = false
        (transaction as TransactionDataDb).withHandle { handle ->
            val foundPlayers  =
                    handle.createQuery("select id, name, score from player order by score desc offset :skip limit :limit")
                    .bind("skip", skip)
                    .bind("limit", limit + 1)
                    .mapTo<Player>().list()
            var found = 0
            foundPlayers.forEach {
                found++
                if (found <= limit)
                    players.add(it)
                else
                    hasMore = true
            }
        }
        return ListOfData(players, hasMore)
    }
}
