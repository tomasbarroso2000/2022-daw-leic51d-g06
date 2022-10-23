package pt.isel.leic.daw.explodingbattleships.http.models

import pt.isel.leic.daw.explodingbattleships.domain.ShipState

data class FleetStateOutputModel(
    val fleet: List<ShipState>
)