package pt.isel.leic.daw.explodingbattleships.domain

data class GameTypeOutcome(
    val name: String,
    val boardSize: Int,
    val shotsPerRound: Int,
    val layoutDefTimeInSecs: Int,
    val shootingTimeInSecs: Int,
    val fleet: List<ShipType>
)