package pt.isel.leic.daw.explodingbattleships.domain

data class GameState(
    val state: String
) { init { require(state.isNotBlank()) } }