package pt.isel.leic.daw.explodingbattleships.domain

data class DataList<T>(val list: List<T>, val hasMore: Boolean)

data class Link(
    val href: String,
    val rel: String,
    val requiresAuth: Boolean
)
