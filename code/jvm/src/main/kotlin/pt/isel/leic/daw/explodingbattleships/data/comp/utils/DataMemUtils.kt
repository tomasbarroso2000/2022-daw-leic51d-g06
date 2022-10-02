package pt.isel.leic.daw.explodingbattleships.data.comp.utils

import org.jdbi.v3.core.Jdbi
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.domain.ShipType
import java.sql.Timestamp
import java.time.Instant

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
    val gameWidth: Int,
    val gameHeight: Int,
    val hitsPerRound: Int,
    val state: String,
    val player1: Int,
    val player2: Int,
    val currPlayer: Int
)

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

data class MockData(
    val players: MutableSet<StoredPlayer> = mutableSetOf(
        StoredPlayer(1, "Leki", "leki@yes.com", 420, 123),
        StoredPlayer(2, "Daizer", "daizer@daizer.daizer", 500, 123)
    ),
    val games: MutableSet<StoredGame> = mutableSetOf(
        StoredGame(1, 10, 10, 1, "completed", 1, 2, 1)
    ),
    val tokens: MutableSet<StoredToken> = mutableSetOf(
        StoredToken("123", 1),
        StoredToken("321", 2)
    ),
    val ships: MutableSet<StoredShip> = mutableSetOf(
        StoredShip("a1", 1, false, "horizontal", 1, 1, "destroyer")
    ),
    val shipTypes: MutableSet<StoredShipType> = mutableSetOf(
        StoredShipType("carrier", 5),
        StoredShipType("battleship", 4),
        StoredShipType("cruiser", 3),
        StoredShipType("submarine", 3),
        StoredShipType("destroyer", 2)
    ),
    val hits: MutableSet<StoredHit> = mutableSetOf()
)

fun StoredPlayer.toPlayer() = Player(id, name, score)
