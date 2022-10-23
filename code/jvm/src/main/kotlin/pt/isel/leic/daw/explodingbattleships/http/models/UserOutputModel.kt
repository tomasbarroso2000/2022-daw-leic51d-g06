package pt.isel.leic.daw.explodingbattleships.http.models

data class UserOutputModel(
    val id: Int,
    val name: String,
    val email: String,
    val score: Int
)