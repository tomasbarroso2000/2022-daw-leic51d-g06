package pt.isel.leic.daw.explodingbattleships.http.models

data class HomeOutputModel(
    val name: String = "Exploding Battleships",
    val version: String = "0.0.0",
    val authors: List<String> = listOf("Leki", "Palmilha", "TBMASTER2000")
)