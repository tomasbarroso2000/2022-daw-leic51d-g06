package pt.isel.leic.daw.explodingbattleships.data.db

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.jdbiObject

class DataDb : Data {
    override val inGameData = InGameDataDb()
    override val gamesData = GamesDataDb()
    override val playersData = PlayersDataDb()

    override fun getTransaction() = TransactionDataDb(jdbiObject.open())
}
