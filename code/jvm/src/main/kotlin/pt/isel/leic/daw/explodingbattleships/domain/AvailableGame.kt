package pt.isel.leic.daw.explodingbattleships.domain

class AvailableGame(
    val id: Int,
    val type: String,
    val state: String,
    val opponent: Int
)