package pt.isel.leic.daw.explodingbattleships.domain

data class Player(val id: Int, val name: String, val score: Int) // potentially add profile pic

data class PlayerInput(
    val name: String?,
    val email: String?,
    val password: String?
)

data class PlayerOutput(
    val id: Int
)
