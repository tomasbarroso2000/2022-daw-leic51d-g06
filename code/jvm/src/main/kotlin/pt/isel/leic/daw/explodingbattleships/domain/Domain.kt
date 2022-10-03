package pt.isel.leic.daw.explodingbattleships.domain

data class ListOfData<T>(val list: List<T>, val hasMore: Boolean)

data class Player(val id: Int, val name: String, val score: Int) // potentially add profile pic

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

data class Layout(
    val gameId: Int?,
    val ships: List<UnverifiedShip>?
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

fun Game.idlePlayer() = if (currPlayer == player1) player2 else player1
fun Game.otherPlayer(playerId: Int) =
    if (player1 == playerId) player2
    else if (player2 == playerId) player1
    else throw IllegalArgumentException("Player not in game")

data class Hits(
    val gameId: Int?,
    val squares: List<UnverifiedSquare>?
)

data class HitOutcome(
    val square: VerifiedSquare,
    val hit: Boolean,
    val destroyedShip: String? = null
)

data class EnterLobbyInput(
    val width: Int,
    val height: Int,
    val hitsPerRound: Int
)
