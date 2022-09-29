package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.data.comp.game.GameDataDb
import pt.isel.leic.daw.explodingbattleships.data.comp.player.PlayerDataDb
import pt.isel.leic.daw.explodingbattleships.data.comp.ranking.RankingDataDb
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.TransactionDataDb
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.connectionInit

class DataDb : Data {
    override val rankingData = RankingDataDb()
    override val gameData = GameDataDb()
    override val playerData = PlayerDataDb()

    override fun getTransaction() = TransactionDataDb(connectionInit())
}
