package pt.isel.leic.daw.explodingbattleships.domain

/**
 * Represents the outcome of a lot of hits
 * @property hitsOutcome the list of single hits outcome
 * @property win if the user won
 */
data class HitsOutcome(
    val hitsOutcome: List<HitOutcome>,
    val win: Boolean
)