package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.DataMem
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppExceptionStatus

class UnauthenticatedServicesTests {
    private val data = DataMem()
    private val services = Services(data).unauthenticatedServices

    @Test
    fun get_number_of_played_games() {
        val number = services.getNumberOfPlayedGames()
        assertEquals(2, number)
    }

    @Test
    fun get_game_state() {
        val state = services.getGameState(1)
        assertEquals("layout_definition", state)
    }

    @Test
    fun get_game_state_of_invalid_game() {
        val exception = assertThrows<AppException> {
            services.getGameState(-1)
        }
        assertEquals("Invalid gameId", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun get_game_state_of_non_existing_game() {
        val exception = assertThrows<AppException> {
            services.getGameState(100)
        }
        assertEquals("Game does not exist", exception.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, exception.status)
    }

    @Test
    fun get_rankings() {
        val rankings = services.getRankings(10, 0)
        assertEquals(4, rankings.list[0].id)
        assertEquals(3, rankings.list[1].id)
    }

    @Test
    fun get_rankings_with_invalid_limit() {
        val exception = assertThrows<AppException> {
            services.getRankings(-1, 0)
        }
        assertEquals("Invalid limit", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun get_rankings_with_invalid_skip() {
        val exception = assertThrows<AppException> {
            services.getRankings(10, -1)
        }
        assertEquals("Invalid skip", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }
}
