package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.domain.*
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

data class StoredUser(
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
    val startedAt: Instant
)

fun StoredGame.toGame(): Game {
    //val time: Int = type.toGameType()?.shootingTimeInSecs ?: 0
    return Game(
        id, type, state, player1, player2, currPlayer, startedAt
    )
}

data class StoredToken(
    val tokenVer: String,
    val userId: Int
)

data class StoredShip(
    val firstSquare: String,
    val name: String,
    val size: Int,
    val nOfHits: Int,
    val destroyed: Boolean,
    val orientation: String,
    val player: Int,
    val game: Int
)

fun StoredShip.toShipState() = ShipState(name, destroyed)
fun StoredShip.toVerifiedShip() = VerifiedShip(name, firstSquare.toVerifiedSquare(), orientation, size)


data class StoredHit(
    val square: String,
    val hitTimestamp: Timestamp,
    val onShip: Boolean,
    val player: Int,
    val game: Int
)

data class StoredLobby(
    val player: Int,
    val gameType: String,
    val enterTime: Instant
)

data class MockData(
    val users: MutableSet<StoredUser> = mutableSetOf(
        StoredUser(1, "Leki", "leki@yes.com", 420, 123),
        StoredUser(2, "Daizer", "daizer@daizer.daizer", 500, 123),
        StoredUser(3, "LordFarquaad", "farquaad@buebuelonge.com", 510, 123),
        StoredUser(4, "GingerbreadMan", "ginger@buebuelonge.com", 520, 123),
        StoredUser(5, "Shrek", "ilovefiona@pantano.com", 10, 123),
        StoredUser(6, "Fiona", "iloveshrek@gmail.com", 10, 123)
    ),
    val games: MutableSet<StoredGame> = mutableSetOf(
        StoredGame(1, "beginner", "layout_definition", 1, 2, 1, Instant.now()),
        StoredGame(2, "experienced", "shooting", 5, 6, 5, Instant.now()),
        StoredGame(3, "beginner", "layout_definition", 1, 2, 1, Instant.now()),
        StoredGame(4, "experienced", "shooting", 3, 4, 3, Instant.now())
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
        StoredShip("a1", "carrier", 5, 0, false, "horizontal", 1, 1),
        StoredShip("b1", "battleship", 4, 0, false, "vertical", 1, 1),
        StoredShip("c2", "cruiser", 3, 0, false, "horizontal", 1, 1),
        StoredShip("b2", "submarine", 3, 0, false, "horizontal", 1, 1),
        StoredShip("d2", "destroyer", 2, 0, false, "vertical", 1, 1),

        StoredShip("a1", "carrier", 5, 0, false, "horizontal", 2, 1),
        StoredShip("b1", "battleship", 5, 0, false, "vertical", 2, 1),
        StoredShip("c2", "cruiser", 3, 0, false, "horizontal",2, 1),
        StoredShip("b2", "submarine", 3, 3, true, "horizontal", 2, 1),
        StoredShip("d2", "destroyer", 2, 2, true, "vertical", 2, 1),

        StoredShip("a1", "carrier", 5, 0, false, "horizontal", 6, 2),
        StoredShip("b1", "battleship", 4, 0, false, "vertical", 6, 2),
        StoredShip("c2", "cruiser", 3, 0, false, "horizontal", 6, 2),
        StoredShip("b2", "submarine", 3, 0, false, "horizontal", 6, 2),
        StoredShip("d2", "destroyer", 2, 0, false, "vertical", 6, 2),

        StoredShip("a1", "destroyer", 2, 0, false, "vertical", 4, 4)
    ),

    val hits: MutableSet<StoredHit> = mutableSetOf(
        StoredHit("f1", Timestamp.from(Instant.now()), false,6, 2)
    ),
    val lobby: MutableSet<StoredLobby> = mutableSetOf(
        StoredLobby(4, "beginner", Instant.now())
    )
)

fun StoredUser.toUser() = User(id, name, email, score, passwordVer)
fun StoredUser.toRanking() = Ranking(id, name, score)
