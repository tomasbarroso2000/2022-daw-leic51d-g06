package pt.isel.leic.daw.explodingbattleships.domain

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val score: Int,
    val passwordVer: Int
) // potentially add profile pic
