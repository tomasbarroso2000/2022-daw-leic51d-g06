package pt.isel.leic.daw.explodingbattleships.utils

interface TokenEncoder {
    fun hash(token: String): String
}