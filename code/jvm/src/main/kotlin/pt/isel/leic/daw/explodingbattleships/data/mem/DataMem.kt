package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.MockData

class DataMem : Data {
    private val mockData = MockData()

    override val inGameData = InGameDataMem(mockData)
    override val gamesData = GamesDataMem(mockData)
    override val playersData = PlayersDataMem(mockData)

    override fun getTransaction() = TransactionDataMem()
}
