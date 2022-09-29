package pt.isel.leic.daw.explodingbattleships.data.comp.player

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutput
import pt.isel.leic.daw.explodingbattleships.domain.PlayerOutput
import pt.isel.leic.daw.explodingbattleships.domain.TokenOutput

interface PlayerData {
    fun getPlayerIdByToken(transaction: Transaction, token: String): Int?

    fun createPlayer(transaction: Transaction, name: String, email: String, password: Int): PlayerOutput?

    fun createToken(transaction: Transaction, playerId: Int): TokenOutput?

    fun enterLobby(transaction: Transaction, playerId: Int): EnterLobbyOutput?
}
