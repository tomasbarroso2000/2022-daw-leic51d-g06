package pt.isel.leic.daw.explodingbattleships.data

import org.jdbi.v3.core.Jdbi
import pt.isel.leic.daw.explodingbattleships.data.comp.game.GameData
import pt.isel.leic.daw.explodingbattleships.data.comp.player.PlayerData
import pt.isel.leic.daw.explodingbattleships.data.comp.ranking.RankingData
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.TransactionDataMem

interface Data {
    val rankingData: RankingData
    val gameData: GameData
    val playerData: PlayerData

    fun getTransaction(): Transaction
}
