package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.mem.DataMem
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyInput
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutput
import pt.isel.leic.daw.explodingbattleships.domain.PlayerOutputModel
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus

class AuthenticatedServicesTests {
    private val data = DataMem()
    private val services = Services(data).authenticatedServices

    @Test
    fun get_player_info() {
        val token = "123"
        val expectedPlayer = PlayerOutputModel(1, "Leki", 420)
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
        val enterLobbyInput = EnterLobbyInput("beginner")
        val expectedOutput = EnterLobbyOutput(false, data.mockData.games.size + 1)
        val actualOutput = services.enterLobby(token, enterLobbyInput)
        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun enter_lobby_without_token() {
        val token = ""
        val enterLobbyInput = EnterLobbyInput("beginner")
        val exception = assertThrows<AppException> {
            services.enterLobby(token, enterLobbyInput)
        }
        assertEquals("No token provided", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun enter_lobby_with_invalid_token() {
        val token = "nope"
        val enterLobbyInput = EnterLobbyInput("beginner")
        val exception = assertThrows<AppException> {
            services.enterLobby(token, enterLobbyInput)
        }
        assertEquals("Invalid token", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

}
