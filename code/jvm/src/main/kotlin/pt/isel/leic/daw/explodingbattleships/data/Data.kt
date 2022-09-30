package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.data.comp.games.GamesData
import pt.isel.leic.daw.explodingbattleships.data.comp.ingame.InGameData
import pt.isel.leic.daw.explodingbattleships.data.comp.players.PlayersData
import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction

interface Data {
    val inGameData: InGameData
    val gamesData: GamesData
    val playersData: PlayersData

    fun getTransaction(): Transaction
}
