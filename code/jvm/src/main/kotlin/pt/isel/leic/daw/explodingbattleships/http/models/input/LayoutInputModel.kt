package pt.isel.leic.daw.explodingbattleships.http.models.input

import pt.isel.leic.daw.explodingbattleships.domain.ShipCreationInfo

/**
 * Represents the information needed for sending a layout
 * @param gameId the game id
 * @param ships the list of information needed for creating a ship
 */
data class LayoutInputModel(
    val gameId: Int,
    val ships: List<ShipCreationInfo>
)