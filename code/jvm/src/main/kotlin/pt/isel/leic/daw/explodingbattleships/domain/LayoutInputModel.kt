package pt.isel.leic.daw.explodingbattleships.domain

enum class LayoutOutcomeStatus { WAITING, STARTED }

data class LayoutInputModel(val gameId: Int, val ships: List<ShipCreationInfo>)

data class LayoutOutcome(
    val status: LayoutOutcomeStatus
)
