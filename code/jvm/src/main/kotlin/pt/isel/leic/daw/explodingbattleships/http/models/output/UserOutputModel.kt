package pt.isel.leic.daw.explodingbattleships.http.models.output

/**
 * Represents the information needed to show the user his information
 * @param id the user id
 * @param name the user's name
 * @param email the user's email
 * @param score the user's score
 */
data class UserOutputModel(
    val id: Int,
    val name: String,
    val email: String,
    val score: Int
)
