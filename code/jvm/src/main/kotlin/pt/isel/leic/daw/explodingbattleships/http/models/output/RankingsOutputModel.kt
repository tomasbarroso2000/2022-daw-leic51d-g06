package pt.isel.leic.daw.explodingbattleships.http.models.output

import pt.isel.leic.daw.explodingbattleships.domain.UserInfo

/**
 * Represents the information needed to show the user the rankings
 * @param rankings the rankings
 * @param hasMore if there are more elements
 */
data class RankingsOutputModel(
    val rankings: List<UserInfo>,
    val hasMore: Boolean
)