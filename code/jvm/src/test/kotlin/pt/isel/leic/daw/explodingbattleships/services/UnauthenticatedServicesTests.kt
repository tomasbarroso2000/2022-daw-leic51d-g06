package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.mem.DataMem
import pt.isel.leic.daw.explodingbattleships.domain.PlayerInput
import pt.isel.leic.daw.explodingbattleships.domain.PlayerOutput
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus

class UnauthenticatedServicesTests {
    private val data = DataMem()
    private val services = Services(data).unauthenticatedServices

    @Test
    fun create_player() {
        val playerInput = PlayerInput(
            "aleixo",
            "aleixo@casapia.pt",
            "OneLoveCasaPia6"
        )
        val expectedOutput = PlayerOutput(7)
        val actualOutput = services.createPlayer(playerInput)
        assertEquals(expectedOutput, actualOutput)
        assertTrue(data.mockData.players.any { it.id == 7})
    }

    @Test
    fun create_player_with_invalid_name() {
        val playerInput = PlayerInput(
            "",
            "aleixo@casapia.pt",
            "OneLoveCasaPia6"
        )
        val exception = assertThrows<AppException> {
            services.createPlayer(playerInput)
        }
        assertEquals("Invalid name", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_invalid_email() {
        val playerInput = PlayerInput(
            "aleixo",
            "",
            "OneLoveCasaPia6"
        )
        val exception = assertThrows<AppException> {
            services.createPlayer(playerInput)
        }
        assertEquals("Invalid email", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_invalid_password() {
        val playerInput = PlayerInput(
            "aleixo",
            "aleixo@casapia.pt",
            ""
        )
        val exception = assertThrows<AppException> {
            services.createPlayer(playerInput)
        }
        assertEquals("Invalid password", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_password_without_numbers() {
        val playerInput = PlayerInput(
            "aleixo",
            "aleixo@casapia.pt",
            "casaPia"
        )
        val exception = assertThrows<AppException> {
            services.createPlayer(playerInput)
        }
        assertEquals("Password doesn't contain numbers", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_password_without_uppercase_letters() {
        val playerInput = PlayerInput(
            "aleixo",
            "aleixo@casapia.pt",
            "casapia6"
        )
        val exception = assertThrows<AppException> {
            services.createPlayer(playerInput)
        }
        assertEquals("Password doesn't contain uppercase letters", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_password_without_lowerscase_letters() {
        val playerInput = PlayerInput(
            "aleixo",
            "aleixo@casapia.pt",
            "CASAPIA6"
        )
        val exception = assertThrows<AppException> {
            services.createPlayer(playerInput)
        }
        assertEquals("Password doesn't contain lowercase letters", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun get_number_of_played_games() {
        val number = services.getNumberOfPlayedGames()
        assertEquals(data.mockData.games.size, number.number)
    }

    @Test
    fun get_game_state() {
        val state = services.getGameState(1)
        assertEquals("layout_definition", state.state)
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
        assertEquals(4, rankings.list.list[0].id)
        assertEquals(3, rankings.list.list[1].id)
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
