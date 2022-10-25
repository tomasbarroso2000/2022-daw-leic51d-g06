package pt.isel.leic.daw.explodingbattleships.http.models.input

import pt.isel.leic.daw.explodingbattleships.domain.Square

data class HitsInputModel(
    val gameId: Int,
    val squares: List<Square>
)
