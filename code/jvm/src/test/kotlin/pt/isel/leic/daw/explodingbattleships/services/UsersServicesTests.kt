package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.mem.DataMem
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutput
import pt.isel.leic.daw.explodingbattleships.domain.User
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus

class UsersServicesTests {
    private val data = DataMem()
    private val services = UsersServices(data)

    @Test
    fun create_user() {
        val name = "aleixo"
        val email = "aleixo@casapia.pt"
        val password = "OneLoveCasaPia6"
        val expectedOutput = 8
        val actualOutput = services.createUser(name, email, password)
        assertEquals(expectedOutput, actualOutput)
        assertTrue(data.mockData.users.any { it.id == 7 })
    }

    @Test
    fun create_player_with_invalid_name() {
        val name = ""
        val email = "aleixo@casapia.pt"
        val password = "OneLoveCasaPia6"
        val exception = assertThrows<AppException> {
            services.createUser(name, email, password)
        }
        assertEquals("Invalid name", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_invalid_email() {
        val name = "aleixo"
        val email = ""
        val password = "OneLoveCasaPia6"
        val exception = assertThrows<AppException> {
            services.createUser(name, email, password)
        }
        assertEquals("Invalid email", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_invalid_email_format() {
        val name = "aleixo"
        val email = "hello"
        val password = "OneLoveCasaPia6"
        val exception = assertThrows<AppException> {
            services.createUser(name, email, password)
        }
        assertEquals("Invalid email format", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_invalid_password() {
        val name = "aleixo"
        val email = "aleixo@casapia.pt"
        val password = ""
        val exception = assertThrows<AppException> {
            services.createUser(name, email, password)
        }
        assertEquals("Invalid password", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_password_without_numbers() {
        val name = "aleixo"
        val email = "aleixo@casapia.pt"
        val password = "OneLoveCasaPia"
        val exception = assertThrows<AppException> {
            services.createUser(name, email, password)
        }
        assertEquals("Password doesn't contain numbers", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_password_without_uppercase_letters() {
        val name = "aleixo"
        val email = "aleixo@casapia.pt"
        val password = "casapia6"
        val exception = assertThrows<AppException> {
            services.createUser(name, email, password)
        }
        assertEquals("Password doesn't contain uppercase letters", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_password_without_lowercase_letters() {
        val name = "aleixo"
        val email = "aleixo@casapia.pt"
        val password = "CASAPIA6"
        val exception = assertThrows<AppException> {
            services.createUser(name, email, password)
        }
        assertEquals("Password doesn't contain lowercase letters", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_token() {
        val email = "iloveshrekalso@gmail.com"
        val password = "shrek"
        assertTrue(services.createToken(email, password).isNotBlank())
    }

    @Test
    fun create_token_with_bad_credentials() {
        val email = "quaqua@gmail.com"
        val password = "shrek"
        val exception = assertThrows<AppException> {
            services.createToken(email, password)
        }
        assertEquals("Bad credentials", exception.message)
    }

    @Test
    fun get_player_info() {
        val token = "123"
        val expectedPlayer = User(1, "Leki", "leki@yes.com", 420, 123)
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

    @Test
    fun enter_lobby() {
        val userId = 3
        val gameType = "beginner"
        val expectedOutput = EnterLobbyOutput(false, data.mockData.games.size + 1)
        val actualOutput = services.enterLobby(userId, gameType)
        assertEquals(expectedOutput, actualOutput)
    }
}
