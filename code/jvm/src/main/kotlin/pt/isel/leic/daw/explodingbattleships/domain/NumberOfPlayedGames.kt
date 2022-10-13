package pt.isel.leic.daw.explodingbattleships.domain

data class NumberOfPlayedGames(
    val number: Int
) { init { require(number >= 0) } }