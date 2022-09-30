package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.data.comp.games.GamesDataDb
import pt.isel.leic.daw.explodingbattleships.data.comp.ingame.InGameDataDb
import pt.isel.leic.daw.explodingbattleships.data.comp.players.PlayersDataDb
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.TransactionDataDb
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.jdbiObject

class DataDb : Data {
    override val inGameData = InGameDataDb()
    override val gamesData = GamesDataDb()
    override val playersData = PlayersDataDb()

    override fun getTransaction() = TransactionDataDb(jdbiObject.open())
}
