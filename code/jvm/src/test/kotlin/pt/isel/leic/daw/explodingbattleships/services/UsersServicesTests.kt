package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.leic.daw.explodingbattleships.data.mem.DataMem
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutcome
import pt.isel.leic.daw.explodingbattleships.domain.User
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus
import pt.isel.leic.daw.explodingbattleships.utils.Sha256TokenEncoder

class UsersServicesTests {
    private val data = DataMem()
    private val services = UsersServices(
        data,
        BCryptPasswordEncoder(),
        Sha256TokenEncoder()
    )

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
    fun create_player_with_repeated_email() {
        val name = "aleixo"
        val email = "leki@yes.com"
        val password = "OneLoveCasaPia6"
        val exception = assertThrows<AppException> {
            services.createUser(name, email, password)
        }
        assertEquals("Email $email is already in use", exception.message)
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
        val expectedPlayer = User(1, "Leki", "leki@yes.com", 420, "yes")
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
    fun enter_lobby_start() {
        val userId = 3
        val gameType = "beginner"
        val expectedOutput = EnterLobbyOutcome(false, data.mockData.games.size + 1)
        val actualOutput = services.enterLobby(userId, gameType)
        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun enter_lobby_wait() {
        val userId = 7
        val gameType = "experienced"
        val expectedOutput = EnterLobbyOutcome(true, 3)
        val actualOutput = services.enterLobby(userId, gameType)
        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun enter_lobby_with_invalid_game_type() {
        val exception = assertThrows<AppException> {
            services.enterLobby(1, "very very hard")
        }
        assertEquals("Invalid game type", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun entered_game() {
        val expectedEnteredGame = 6
        val actualEnteredGame = services.enteredGame(3, 2)
        assertEquals(expectedEnteredGame, actualEnteredGame)
        assertFalse(data.mockData.lobbies.any { it.id == 2 })
    }

    @Test
    fun entered_game_with_invalid_lobby_id() {
        val exception = assertThrows<AppException> {
            services.enteredGame(3, -1)
        }
        assertEquals("Invalid lobby id", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun entered_game_with_non_existing_lobby() {
        val exception = assertThrows<AppException> {
            services.enteredGame(3, 10)
        }
        assertEquals("Lobby doesn't exist", exception.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, exception.status)
    }

    @Test
    fun entered_game_with_lobby_of_someone_else() {
        val exception = assertThrows<AppException> {
            services.enteredGame(1, 2)
        }
        assertEquals("User not in lobby", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }
}