package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.DataMem
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppException

class GameServicesTests {
    private val data = DataMem()
    private val services = Services(data).gameServices

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
}
