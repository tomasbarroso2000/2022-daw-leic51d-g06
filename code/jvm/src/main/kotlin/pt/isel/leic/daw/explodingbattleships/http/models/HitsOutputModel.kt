package pt.isel.leic.daw.explodingbattleships.http.models

import pt.isel.leic.daw.explodingbattleships.domain.HitOutcome

data class HitsOutputModel(
    val hitsOutcome: List<HitOutcome>,
    val win: Boolean
)