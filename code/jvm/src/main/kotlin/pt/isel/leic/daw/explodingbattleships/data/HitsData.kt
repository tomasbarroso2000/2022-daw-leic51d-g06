package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.Hit
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare

interface HitsData {
    fun createHit(transaction: Transaction, square: VerifiedSquare, gameId: Int, playerId: Int, onShip: Boolean)

    fun getHits(transaction: Transaction, gameId: Int, userId: Int): List<Hit>
}