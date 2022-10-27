package pt.isel.leic.daw.explodingbattleships.http.models.input

/**
 * Represents the information needed for creating a user
 * @param name the user's name
 * @param email the user's email
 * @param password the user's password
 */
data class UserInputModel(
    val name: String,
    val email: String,
    val password: String
)
