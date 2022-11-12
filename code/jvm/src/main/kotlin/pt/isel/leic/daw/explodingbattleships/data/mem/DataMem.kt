package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.Data

class DataMem : Data {

    /**
     * The mock data used for tests
     */
    val mockData = MockData()

    override val usersData = UsersDataMem(mockData)
    override val gamesData = GamesDataMem(mockData)
    override val lobbiesData = LobbiesDataMem(mockData)
    override val shipsData = ShipsDataMem(mockData)
    override val hitsData = HitsDataMem(mockData)
    override val gameTypesData = GameTypesDataMem(mockData)
    override val shipTypesData = ShipTypesDataMem(mockData)

    override fun getTransaction() = TransactionDataMem()
}