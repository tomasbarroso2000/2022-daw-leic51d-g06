package pt.isel.leic.daw.explodingbattleships.db

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pt.isel.leic.daw.explodingbattleships.utils.testAndRollback
import kotlin.test.assertEquals

class UsersDataDbTests {

    @Test
    fun can_create_and_retrieve() = testAndRollback { transaction, db ->

        val user1 = db.usersData.getUserFromEmail(transaction, "leki@leki.leki") ?: fail("user not in database")

        assertEquals(1, user1.id)
        assertEquals("Leki", user1.name)
        assertEquals("leki@leki.leki", user1.email)

        val user2 = db.usersData.getUserFromEmail(transaction, "palma@palma.palma") ?: fail("user not in database")

        assertEquals(2, user2.id)
        assertEquals("Palma", user2.name)
        assertEquals("palma@palma.palma", user2.email)

        val createdUserId = db.usersData.createUser(transaction, "Daizer", "daizer@ric.com", "yes")

        val createdUser = db.usersData.getUserFromEmail(transaction, "daizer@ric.com")

        assertEquals(createdUserId, createdUser?.id)
        assertEquals("Daizer", createdUser?.name)
        assertEquals("daizer@ric.com", createdUser?.email)
    }
}