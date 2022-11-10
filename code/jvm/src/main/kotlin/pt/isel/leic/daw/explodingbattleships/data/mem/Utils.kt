package pt.isel.leic.daw.explodingbattleships.data.mem

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.GameType
import pt.isel.leic.daw.explodingbattleships.domain.Hit
import pt.isel.leic.daw.explodingbattleships.domain.Lobby
import pt.isel.leic.daw.explodingbattleships.domain.Ranking
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.ShipSpec
import pt.isel.leic.daw.explodingbattleships.domain.User
import pt.isel.leic.daw.explodingbattleships.utils.Sha256TokenEncoder
import java.time.Instant

/**
 * To be thrown by the DataMem module when an error is detected
 * @property message the error message
 */
data class DataException(
    override val message: String? = null
) : Exception()

/**
 * Gets a sublist with given limit and skip values
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

/**
 * Transforms a [User] into a [Ranking]
 */
fun User.toRanking() = Ranking(id, name, score)

/**
 * Represents the tokens stored in memory
 */
data class StoredToken(
    val tokenVer: String,
    val userId: Int
)

val passwordEncoder = BCryptPasswordEncoder()
val tokenEncoder = Sha256TokenEncoder()

/**
 * The mock data used for tests
 * @property users the users in memory
 * @property games the games in memory
 * @property tokens the tokens in memory
 * @property ships the ships in memory
 * @property hits the hits in memory,
 * @property lobbies the lobbies in memory
 */
data class MockData(
    val users: MutableSet<User> = mutableSetOf(
        User(1, "Leki", "leki@yes.com", 420, "yes"),
        User(2, "Daizer", "daizer@daizer.daizer", 500, "yes"),
        User(3, "LordFarquaad", "farquaad@buebuelonge.com", 510, "yes"),
        User(4, "GingerbreadMan", "ginger@buebuelonge.com", 520, "yes"),
        User(5, "Shrek", "ilovefiona@pantano.com", 10, "yes"),
        User(6, "Fiona", "iloveshrek@gmail.com", 10, "yes"),
        User(7, "Burro", "iloveshrekalso@gmail.com", 10, passwordEncoder.encode("shrek"))
    ),
    val gameTypes: MutableList<GameType> = mutableListOf(
        GameType("beginner", 10, 1, 60, 60),
        GameType("experienced", 12, 5, 60, 30),
        GameType("expert", 15, 6, 30, 30)
    ),
    val ship_types: MutableList<ShipSpec> = mutableListOf(
        ShipSpec("carrier", 6, "beginner"),
        ShipSpec("battleship", 5, "beginner"),
        ShipSpec("cruiser", 4, "beginner"),
        ShipSpec("submarine", 4, "beginner"),
        ShipSpec("destroyer", 3, "beginner"),
        ShipSpec("carrier", 5, "experienced"),
        ShipSpec("battleship", 4, "experienced"),
        ShipSpec("cruiser", 3, "experienced"),
        ShipSpec("submarine", 3, "experienced"),
        ShipSpec("destroyer", 2, "experienced"),
        ShipSpec("carrier", 5, "expert"),
        ShipSpec("battleship", 4, "expert"),
        ShipSpec("destroyer", 3, "expert")
    ),
    val games: MutableSet<Game> = mutableSetOf(
        Game(1, "beginner", "layout_definition", 1, 2, 1, Instant.now()),
        Game(2, "experienced", "shooting", 5, 6, 5, Instant.now()),
        Game(3, "beginner", "layout_definition", 1, 2, 1, Instant.now()),
        Game(4, "experienced", "shooting", 3, 4, 3, Instant.now()),
        Game(5, "beginner", "layout_definition", 3, 7, 3, Instant.now()),
        Game(6, "experienced", "layout_definition", 3, 7, 3, Instant.now())
    ),
    val tokens: MutableSet<StoredToken> = mutableSetOf(
        StoredToken(tokenEncoder.hash("123"), 1),
        StoredToken(tokenEncoder.hash("321"), 2),
        StoredToken(tokenEncoder.hash("fiona"), 3),
        StoredToken(tokenEncoder.hash("homem-queque"), 4),
        StoredToken(tokenEncoder.hash("buro"), 5),
        StoredToken(tokenEncoder.hash("shrekinho"), 6)
    ),
    val ships: MutableSet<Ship> = mutableSetOf(
        Ship("a1", "carrier", 5, 0, false, "horizontal", 1, 1),
        Ship("b1", "battleship", 4, 0, false, "vertical", 1, 1),
        Ship("c2", "cruiser", 3, 0, false, "horizontal", 1, 1),
        Ship("b2", "submarine", 3, 0, false, "horizontal", 1, 1),
        Ship("d2", "destroyer", 2, 0, false, "vertical", 1, 1),

        Ship("a1", "carrier", 5, 0, false, "horizontal", 2, 1),
        Ship("b1", "battleship", 5, 0, false, "vertical", 2, 1),
        Ship("c2", "cruiser", 3, 0, false, "horizontal", 2, 1),
        Ship("b2", "submarine", 3, 3, true, "horizontal", 2, 1),
        Ship("d2", "destroyer", 2, 2, true, "vertical", 2, 1),

        Ship("a1", "carrier", 5, 0, false, "horizontal", 6, 2),
        Ship("b1", "battleship", 4, 0, false, "vertical", 6, 2),
        Ship("c2", "cruiser", 3, 0, false, "horizontal", 6, 2),
        Ship("b2", "submarine", 3, 0, false, "horizontal", 6, 2),
        Ship("d2", "destroyer", 2, 0, false, "vertical", 6, 2),

        Ship("a1", "destroyer", 2, 0, false, "vertical", 4, 4),

        Ship("a1", "carrier", 5, 0, false, "horizontal", 3, 5),
        Ship("b1", "battleship", 4, 0, false, "vertical", 3, 5),
        Ship("c2", "cruiser", 3, 0, false, "horizontal", 3, 5),
        Ship("b2", "submarine", 3, 0, false, "horizontal", 3, 5),
        Ship("d2", "destroyer", 2, 0, false, "vertical", 3, 5)
    ),

    val hits: MutableSet<Hit> = mutableSetOf(
        Hit("f1", Instant.now(), false, 6, 2)
    ),
    val lobbies: MutableSet<Lobby> = mutableSetOf(
        Lobby(1, 4, "beginner", Instant.now(), null),
        Lobby(2, 3, "expert", Instant.now(), 6)
    )
)