package pt.isel.leic.daw.explodingbattleships.http.models.input

import pt.isel.leic.daw.explodingbattleships.domain.ShipCreationInfo

data class LayoutInputModel(
    val gameId: Int,
    val ships: List<ShipCreationInfo>
)
