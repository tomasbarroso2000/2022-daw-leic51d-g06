package pt.isel.leic.daw.explodingbattleships.data.comp.utils

import pt.isel.leic.daw.explodingbattleships.domain.*
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.toGameType
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount

/**
 * To be thrown by the DataMem module when an error is detected
 * @property message the error message
 */
data class DataException(
    override val message: String? = null
) : Exception()

/**
 * Get a sublist with given limit and skip values
 * @param list the initial list
 * @param limit the size of the sublist
 * @param skip the first index of the initial list to be considered
 * @return the sublist
 */
fun <T> getSublist(list: List<T>, limit: Int, skip: Int): List<T> {
    val newList = mutableListOf<T>()
    for (i in skip until list.size) {
        if (i < skip + limit) {
            newList.add(list[i])
        } else {
            break
        }
    }
    return newList
}

/**
 * Checks if there are more tuples
 * @param count the total number of tuples found
 * @param limit the limit of tuples defined
 * @param skip the skip value defined
 * @return true if there are more tuples
 */
fun hasMore(count: Int, limit: Int, skip: Int) = count > skip + limit

data class StoredPlayer(
    val id: Int,
    val name: String,
    val email: String,
    val score: Int,
    val passwordVer: Int
)

data class StoredGame(
    val id: Int,
    val type: String,
    val state: String,
    val player1: Int,
    val player2: Int,
    val currPlayer: Int,
    val deadline: Instant?
)

fun StoredGame.toGame(): Game {
    val time: Int = type.toGameType()?.shootingTimeInSecs ?: 0
    return Game(
        id, type, state, player1, player2, currPlayer,
        Instant.ofEpochSecond(time.toLong())
    )
}

data class StoredToken(
    val tokenVer: String,
    val player: Int
)

data class StoredShip(
    val firstSquare: String,
    val nOfHits: Int,
    val destroyed: Boolean,
    val orientation: String,
    val player: Int,
    val game: Int,
    val shipType: String
)

fun StoredShip.toShipState() = ShipState(shipType, destroyed)
fun StoredShip.toVerifiedShip() = VerifiedShip(shipType, firstSquare.toVerifiedSquare(), orientation)

data class StoredShipType(
    val typeName: String,
    val shipSize: Int
)

data class StoredHit(
    val square: String,
    val hitTimestamp: Timestamp,
    val player: Int,
    val game: Int
)

data class StoredLobby(
    val player: Int,
    val gameType: String,
    val enterTime: Instant
)

data class MockData(
    val players: MutableSet<StoredPlayer> = mutableSetOf(
        StoredPlayer(1, "Leki", "leki@yes.com", 420, 123),
        StoredPlayer(2, "Daizer", "daizer@daizer.daizer", 500, 123),
        StoredPlayer(3, "LordFarquaad", "farquaad@buebuelonge.com", 510, 123),
        StoredPlayer(4, "GingerbreadMan", "ginger@buebuelonge.com", 520, 123),
        StoredPlayer(5, "Shrek", "ilovefiona@pantano.com", 10, 123),
        StoredPlayer(6, "Fiona", "iloveshrek@gmail.com", 10, 123)
    ),
    val games: MutableSet<StoredGame> = mutableSetOf(
        StoredGame(1, "beginner", "layout_definition", 1, 2, 1, Instant.ofEpochSecond(20)),
        StoredGame(2, "experienced", "shooting", 5, 6, 5, Instant.ofEpochSecond(20))
    ),
    val tokens: MutableSet<StoredToken> = mutableSetOf(
        StoredToken("123", 1),
        StoredToken("321", 2),
        StoredToken("fiona", 3),
        StoredToken("homem-queque", 4),
        StoredToken("buro", 5),
        StoredToken("shrekinho", 6)
    ),
    val ships: MutableSet<StoredShip> = mutableSetOf(
        StoredShip("a1", 0, false, "horizontal", 1, 1, "carrier"),
        StoredShip("b1", 0, false, "vertical", 1, 1, "battleship"),
        StoredShip("c2", 0, false, "horizontal", 1, 1, "cruiser"),
        StoredShip("b2", 0, false, "horizontal", 1, 1, "submarine"),
        StoredShip("d2", 0, false, "vertical", 1, 1, "destroyer"),

        StoredShip("a1", 0, false, "horizontal", 2, 1, "carrier"),
        StoredShip("b1", 0, false, "vertical", 2, 1, "battleship"),
        StoredShip("c2", 0, false, "horizontal", 2, 1, "cruiser"),
        StoredShip("b2", 3, true, "horizontal", 2, 1, "submarine"),
        StoredShip("d2", 2, true, "vertical", 2, 1, "destroyer"),

        StoredShip("a1", 0, false, "horizontal", 6, 2, "carrier"),
        StoredShip("b1", 0, false, "vertical", 6, 2, "battleship"),
        StoredShip("c2", 0, false, "horizontal", 6, 2, "cruiser"),
        StoredShip("b2", 0, false, "horizontal", 6, 2, "submarine"),
        StoredShip("d2", 0, false, "vertical", 6, 2, "destroyer")
    ),
    val shipTypes: MutableSet<StoredShipType> = mutableSetOf(
        StoredShipType("carrier", 5),
        StoredShipType("battleship", 4),
        StoredShipType("cruiser", 3),
        StoredShipType("submarine", 3),
        StoredShipType("destroyer", 2)
    ),
    val hits: MutableSet<StoredHit> = mutableSetOf(
        StoredHit("f1", Timestamp.from(Instant.now()), 6, 2)
    ),
    val lobby: MutableSet<StoredLobby> = mutableSetOf(
        StoredLobby(4, "beginner", Instant.now())
    )
)

fun StoredPlayer.toPlayer() = Player(id, name, score)
