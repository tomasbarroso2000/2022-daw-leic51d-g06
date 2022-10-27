package pt.isel.leic.daw.explodingbattleships.domain

/**
 * Represents a list of data
 * @property list the list of data
 * @property hasMore if the original list has more elements
 */
data class DataList<T>(val list: List<T>, val hasMore: Boolean)