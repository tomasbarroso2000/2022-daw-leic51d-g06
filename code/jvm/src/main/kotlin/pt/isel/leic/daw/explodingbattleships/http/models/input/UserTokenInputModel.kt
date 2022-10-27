package pt.isel.leic.daw.explodingbattleships.http.models.input

/**
 * Represents the information needed for creating a user token
 * @param email the user's email
 * @param password the user's password
 */
data class UserTokenInputModel(
    val email: String,
    val password: String
)
