package pt.isel.leic.daw.explodingbattleships.services.comp

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.ListOfData
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.checkLimitAndSkip
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.doService

class RankingServices(private val data: Data) {
    fun getRankings(limit: Int, skip: Int): ListOfData<Player> = doService(data) { transaction ->
        checkLimitAndSkip(limit, skip)
        data.rankingData.getRankings(transaction, limit, skip)
    }
}
