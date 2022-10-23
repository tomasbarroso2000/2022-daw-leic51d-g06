package pt.isel.leic.daw.explodingbattleships.http.models

import pt.isel.leic.daw.explodingbattleships.domain.DataList
import pt.isel.leic.daw.explodingbattleships.domain.Ranking

data class RankingsOutputModel(val rankings: DataList<Ranking>)