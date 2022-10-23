package pt.isel.leic.daw.explodingbattleships.domain

data class User(val id: Int, val name: String, val score: Int) // potentially add profile pic

data class UserInput(
    val name: String?,
    val email: String?,
    val password: String?
)

data class UserOutput(
    val id: Int
)
