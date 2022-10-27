package pt.isel.leic.daw.explodingbattleships.domain

/**
 * Represents a user's ranking
 * @property id the user's id
 * @property name the user's name
 * @property score the user's score
 */
data class Ranking(val id: Int, val name: String, val score: Int)