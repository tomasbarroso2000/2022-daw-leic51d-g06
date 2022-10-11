package pt.isel.leic.daw.explodingbattleships.domain

enum class LayoutOutcomeStatus { WAITING, STARTED }

data class Layout(val gameId: Int?, val ships: List<UnverifiedShip>?)

data class LayoutOutcome(
    val status: LayoutOutcomeStatus
)
