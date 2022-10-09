package pt.isel.leic.daw.explodingbattleships.domain

data class ListOfData<T>(val list: List<T>, val hasMore: Boolean)

data class Link(
    val href: String,
    val rel: String,
    val requiresAuth: Boolean
)

data class SystemInfo(
    val name: String = "Exploding Battleships",
    val version: String = "0.0.0",
    val authors: List<String> = listOf("Leki", "Palmilha", "TBMASTER2000"),
    val links: List<Link> = listOf(
        Link("...", "authenticate", false),
        Link("...", "image", false),
        Link("...", "user-home", true)
    )
)
