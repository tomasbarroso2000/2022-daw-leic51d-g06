package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.data.comp.game.GameDataMem
import pt.isel.leic.daw.explodingbattleships.data.comp.player.PlayerDataMem
import pt.isel.leic.daw.explodingbattleships.data.comp.ranking.RankingDataMem
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.TransactionDataMem
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.MockData

class DataMem : Data {
    private val mockData = MockData()

    override val rankingData = RankingDataMem(mockData)
    override val gameData = GameDataMem(mockData)
    override val playerData = PlayerDataMem(mockData)

    override fun getTransaction() = TransactionDataMem()
}
