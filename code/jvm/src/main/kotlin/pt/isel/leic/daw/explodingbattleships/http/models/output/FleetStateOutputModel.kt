package pt.isel.leic.daw.explodingbattleships.http.models.output

import pt.isel.leic.daw.explodingbattleships.domain.ShipState

/**
 * Represents the information needed to show the user the fleet state
 * @param fleet the list of ship's state
 */
data class FleetStateOutputModel(
    val fleet: List<ShipState>
)
