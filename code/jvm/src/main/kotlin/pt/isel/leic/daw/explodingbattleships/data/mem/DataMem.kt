package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.Data

class DataMem : Data {
    val mockData = MockData()

    override val inGameData = InGameDataMem(mockData)
    override val gamesData = GamesDataMem(mockData)
    override val playersData = PlayersDataMem(mockData)

    override fun getTransaction() = TransactionDataMem()
}
