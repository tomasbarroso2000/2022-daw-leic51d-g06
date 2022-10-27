package pt.isel.leic.daw.explodingbattleships.data

interface Data {
    val usersData: UsersData
    val gamesData: GamesData
    val lobbiesData: LobbiesData
    val shipsData: ShipsData
    val hitsData: HitsData

    /**
     * Gets a transaction
     * @return the transaction
     */
    fun getTransaction(): Transaction
}