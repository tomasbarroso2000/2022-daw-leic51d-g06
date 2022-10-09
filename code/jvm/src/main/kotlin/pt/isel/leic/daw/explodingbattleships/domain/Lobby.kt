package pt.isel.leic.daw.explodingbattleships.domain

data class EnterLobbyInput(
    val width: Int?,
    val height: Int?,
    val hitsPerRound: Int?
)

data class EnterLobbyOutput(
    val entered: Boolean
)
