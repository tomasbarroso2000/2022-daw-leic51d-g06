package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.DataMem
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppException

class UnauthenticatedServicesTests {
    private val data = DataMem()
    private val services = Services(data).unauthenticatedServices

    @Test
    fun get_number_of_played_games() {
        val number = services.getNumberOfPlayedGames()
        assert(number == 1)
    }

    @Test
    fun get_game_state() {
        val state = services.getGameState(1)
        assert(state == "completed")
    }

    @Test
    fun get_game_state_of_invalid_game() {
        val exception = assertThrows<AppException> {
            services.getGameState(-1)
        }
        assert(exception.message == "Invalid gameId")
    }

    @Test
    fun get_game_state_of_non_existing_game() {
        val exception = assertThrows<AppException> {
            services.getGameState(100)
        }
        assert(exception.message == "Game does not exist")
    }

    @Test
    fun get_rankings() {
        val rankings = services.getRankings(10, 0)
        assert(rankings.list[0].id == 2)
        assert(rankings.list[1].id == 1)
    }

    @Test
    fun get_rankings_with_invalid_limit() {
        val exception = assertThrows<AppException> {
            services.getRankings(-1, 0)
        }
        assert(exception.message == "Invalid limit")
    }
}
