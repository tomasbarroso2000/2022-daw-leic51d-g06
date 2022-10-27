package pt.isel.leic.daw.explodingbattleships.db

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pt.isel.leic.daw.explodingbattleships.utils.testAndRollback
import kotlin.test.assertEquals

class GamesDataDbTests {

    @Test
    fun can_create_and_retrieve() = testAndRollback { transaction, db ->

        val game1 = db.gamesData.getGame(transaction, 1) ?: fail("game 1 not in database")

        assertEquals(1, game1.id)
        assertEquals("beginner", game1.type)
        assertEquals("layout_definition", game1.state)

        val game2 = db.gamesData.getGame(transaction, 2) ?: fail("game 2 not in database")

        assertEquals(2, game2.id)
        assertEquals("experienced", game2.type)
        assertEquals("shooting", game2.state)

        val createdGameId = db.gamesData.createGame(transaction, "expert", 1, 2)

        val createdGame = db.gamesData.getGame(transaction, createdGameId)

        assertEquals(createdGameId, createdGame?.id)
        assertEquals("expert", createdGame?.type)
        assertEquals("layout_definition", createdGame?.state)
    }
}
