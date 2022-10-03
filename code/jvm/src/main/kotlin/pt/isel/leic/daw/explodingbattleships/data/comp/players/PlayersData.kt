package pt.isel.leic.daw.explodingbattleships.data.comp.players

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutput
import pt.isel.leic.daw.explodingbattleships.domain.ListOfData
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.domain.PlayerOutput
import pt.isel.leic.daw.explodingbattleships.domain.TokenOutput

interface PlayersData {
    fun getPlayerIdFromToken(transaction: Transaction, token: String): Int?

    fun createPlayer(transaction: Transaction, name: String, email: String, password: Int): PlayerOutput?

    fun createToken(transaction: Transaction, playerId: Int): TokenOutput?

    fun getRankings(transaction: Transaction, limit: Int, skip: Int): ListOfData<Player>

    fun enterLobby(transaction: Transaction, playerId: Int, width: Int, height: Int, hitsPerRound: Int): EnterLobbyOutput?
}
