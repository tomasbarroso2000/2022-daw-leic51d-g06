package pt.isel.leic.daw.explodingbattleships.domain

/**
 * Represents a user's information
 * @property id the user's id
 * @property name the user's name
 * @property score the user's score
 */
data class UserInfo(val id: Int, val name: String, val score: Int)