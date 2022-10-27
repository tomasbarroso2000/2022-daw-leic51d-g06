package pt.isel.leic.daw.explodingbattleships.http.models.output

import pt.isel.leic.daw.explodingbattleships.domain.DataList
import pt.isel.leic.daw.explodingbattleships.domain.Ranking

/**
 * Represents the information needed to show the user the rankings
 * @param rankings the rankings
 */
data class RankingsOutputModel(val rankings: DataList<Ranking>)