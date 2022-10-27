package pt.isel.leic.daw.explodingbattleships.http.models.output

import pt.isel.leic.daw.explodingbattleships.domain.LayoutOutcomeStatus

/**
 * Represents the information needed to show the user the layout outcome status
 * @param status the layout outcome status
 */
class LayoutOutputModel(
    val status: LayoutOutcomeStatus
)
