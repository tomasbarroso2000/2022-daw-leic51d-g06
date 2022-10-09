package pt.isel.leic.daw.explodingbattleships.data

interface Data {
    val inGameData: InGameData
    val gamesData: GamesData
    val playersData: PlayersData

    fun getTransaction(): Transaction
}
