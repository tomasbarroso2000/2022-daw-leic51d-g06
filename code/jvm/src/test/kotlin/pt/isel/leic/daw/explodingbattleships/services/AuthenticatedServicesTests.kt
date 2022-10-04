package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.DataMem
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyInput
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutput
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppExceptionStatus

class AuthenticatedServicesTests {
    private val data = DataMem()
    private val services = Services(data).authenticatedServices

    @Test
    fun get_player_info() {
        val token = "123"
        val expectedPlayer = Player(1, "Leki", 420)
        val actualPlayer = services.getPlayerInfo(token)
        assertEquals(expectedPlayer, actualPlayer)
    }

    @Test
    fun get_player_info_without_token() {
        val token = ""
        val exception = assertThrows<AppException> {
            services.getPlayerInfo(token)
        }
        assertEquals("No token provided", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun get_player_info_with_invalid_token() {
        val token = "nope"
        val exception = assertThrows<AppException> {
            services.getPlayerInfo(token)
        }
        assertEquals("Invalid token", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun enter_lobby() {
        val token = "fiona"
        val enterLobbyInput = EnterLobbyInput(10, 10, 1)
        val expectedOutput = EnterLobbyOutput(true)
        val actualOutput = services.enterLobby(token, enterLobbyInput)
        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun enter_lobby_without_token() {
        val token = ""
        val enterLobbyInput = EnterLobbyInput(10, 10, 1)
        val exception = assertThrows<AppException> {
            services.enterLobby(token, enterLobbyInput)
        }
        assertEquals("No token provided", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun enter_lobby_with_invalid_token() {
        val token = "nope"
        val enterLobbyInput = EnterLobbyInput(10, 10, 1)
        val exception = assertThrows<AppException> {
            services.enterLobby(token, enterLobbyInput)
        }
        assertEquals("Invalid token", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun enter_lobby_with_player_already_in_a_game() {
        val token = "123"
        val enterLobbyInput = EnterLobbyInput(10, 10, 1)
        val exception = assertThrows<AppException> {
            services.enterLobby(token, enterLobbyInput)
        }
        assertEquals("Player already in a game", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun enter_lobby_with_player_already_in_lobby() {
        val token = "homem-queque"
        val enterLobbyInput = EnterLobbyInput(10, 10, 1)
        val exception = assertThrows<AppException> {
            services.enterLobby(token, enterLobbyInput)
        }
        assertEquals("Player already in lobby", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun enter_lobby_with_invalid_board_width() {
        val token = "fiona"
        val enterLobbyInput = EnterLobbyInput(5, 10, 1)
        val exception = assertThrows<AppException> {
            services.enterLobby(token, enterLobbyInput)
        }
        assertEquals("Invalid board width", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun enter_lobby_with_invalid_board_height() {
        val token = "fiona"
        val enterLobbyInput = EnterLobbyInput(10, 5, 1)
        val exception = assertThrows<AppException> {
            services.enterLobby(token, enterLobbyInput)
        }
        assertEquals("Invalid board height", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun enter_lobby_with_invalid_hits_per_round() {
        val token = "fiona"
        val enterLobbyInput = EnterLobbyInput(10, 10, -1)
        val exception = assertThrows<AppException> {
            services.enterLobby(token, enterLobbyInput)
        }
        assertEquals("Invalid hits per round", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }
}
