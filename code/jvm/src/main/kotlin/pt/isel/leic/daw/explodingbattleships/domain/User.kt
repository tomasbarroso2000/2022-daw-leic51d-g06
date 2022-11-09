package pt.isel.leic.daw.explodingbattleships.domain

/**
 * Represents a user
 * @property id the user's id
 * @property name the user's name
 * @property email the user's email
 * @property score the user's score
 * @property passwordVer the user's hashed password
 */
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val score: Int,
    val passwordVer: String
) // potentially add profile pic