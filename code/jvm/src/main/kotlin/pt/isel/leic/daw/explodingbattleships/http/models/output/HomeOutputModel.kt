package pt.isel.leic.daw.explodingbattleships.http.models.output

/**
 * Represents the information needed to show the user the home resource
 * @param name the app name
 * @param version the app version
 * @param authors the app authors
 */
data class HomeOutputModel(
    val name: String = "Exploding Battleships",
    val version: String = "0.0.0",
    val authors: List<String> = listOf("Leki", "Palmilha", "TBMASTER2000")
)
