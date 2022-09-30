package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.data.comp.games.GamesDataMem
import pt.isel.leic.daw.explodingbattleships.data.comp.ingame.InGameDataMem
import pt.isel.leic.daw.explodingbattleships.data.comp.players.PlayersDataMem
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.TransactionDataMem
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.MockData

class DataMem : Data {
    private val mockData = MockData()

    override val inGameData = InGameDataMem(mockData)
    override val gamesData = GamesDataMem(mockData)
    override val playersData = PlayersDataMem(mockData)

    override fun getTransaction() = TransactionDataMem()
}
