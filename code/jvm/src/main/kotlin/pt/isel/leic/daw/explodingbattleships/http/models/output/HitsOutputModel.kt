package pt.isel.leic.daw.explodingbattleships.http.models.output

import pt.isel.leic.daw.explodingbattleships.domain.HitOutcome

/**
 * Represents the information needed to show the user the hits outcome
 * @param hitsOutcome the list of hit's outcome
 * @param win if the player won
 */
data class HitsOutputModel(
    val hitsOutcome: List<HitOutcome>,
    val win: Boolean
)
