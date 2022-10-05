package pt.isel.leic.daw.explodingbattleships.domain

data class Layout(
    val gameId: Int?,
    val ships: List<UnverifiedShip>?
)