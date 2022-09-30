package pt.isel.leic.daw.explodingbattleships.domain

data class ListOfData<T>(val list: List<T>, val hasMore: Boolean)

data class Player(val id: Int, val name: String, val score: Int) // potentially add profile pic

data class Link(
    val href: String,
    val rel: String,
    val requiresAuth: Boolean
)

data class Home(
    val name: String = "Exploding Battleships",
    val version: String = "0.0.0",
    val authors: List<String> = listOf("Leki", "Palmilha", "TBMASTER2000"),
    val links: List<Link> = listOf(
        Link("...", "authenticate", false),
        Link("...", "image", false),
        Link("...", "user-home", true)
    )
)

data class PlayerInput(
    val name: String?,
    val email: String?,
    val password: String?
)

data class PlayerOutput(
    val id: Int
)

data class TokenOutput(
    val token: String
)

data class EnterLobbyOutput(
    val entered: Boolean
)

data class Ship(
    val name: String?,
    val square: Square?,
    val orientation: String?,
)

data class Layout(
    val gameId: Int?,
    val ships: List<Ship>?
)

data class Game(
    val id: Int,
    val width: Int,
    val height: Int,
    val hitsPerRound: Int,
    val state: String,
    val player1: Int,
    val player2: Int,
    val currPlayer: Int
)

fun Game.otherPlayer() = if (currPlayer == player1) player2 else player1

enum class ShipType(val shipName: String, val size: Int) {
    CARRIER("carrier", 5),
    BATTLESHIP("battleship", 4),
    CRUISER("cruiser", 3),
    SUBMARINE("submarine", 3),
    DESTROYER("destroyer", 2)
}

data class Hits(
    val gameId: Int?,
    val squares: List<Square>?
)

data class HitOutcome(
    val square: Square,
    val hit: Boolean,
    val destroyedShip: String?
)
