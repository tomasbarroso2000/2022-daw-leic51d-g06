package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.mem.DataMem
import pt.isel.leic.daw.explodingbattleships.domain.*
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus

class PlayersServicesTests {
    private val data = DataMem()
    private val services = Services(data).usersServices

    @Test
    fun create_player() {
        val playerInput = UserInput(
            "aleixo",
            "aleixo@casapia.pt",
            "OneLoveCasaPia6"
        )
        val expectedOutput = UserOutput(7)
        val actualOutput = services.createUser(playerInput)
        Assertions.assertEquals(expectedOutput, actualOutput)
        Assertions.assertTrue(data.mockData.players.any { it.id == 7 })
    }

    @Test
    fun create_player_with_invalid_name() {
        val playerInput = UserInput(
            "",
            "aleixo@casapia.pt",
            "OneLoveCasaPia6"
        )
        val exception = assertThrows<AppException> {
            services.createUser(playerInput)
        }
        Assertions.assertEquals("Invalid name", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_invalid_email() {
        val playerInput = UserInput(
            "aleixo",
            "",
            "OneLoveCasaPia6"
        )
        val exception = assertThrows<AppException> {
            services.createUser(playerInput)
        }
        Assertions.assertEquals("Invalid email", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_invalid_password() {
        val playerInput = UserInput(
            "aleixo",
            "aleixo@casapia.pt",
            ""
        )
        val exception = assertThrows<AppException> {
            services.createUser(playerInput)
        }
        Assertions.assertEquals("Invalid password", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_password_without_numbers() {
        val playerInput = UserInput(
            "aleixo",
            "aleixo@casapia.pt",
            "casaPia"
        )
        val exception = assertThrows<AppException> {
            services.createUser(playerInput)
        }
        Assertions.assertEquals("Password doesn't contain numbers", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_password_without_uppercase_letters() {
        val playerInput = UserInput(
            "aleixo",
            "aleixo@casapia.pt",
            "casapia6"
        )
        val exception = assertThrows<AppException> {
            services.createUser(playerInput)
        }
        Assertions.assertEquals("Password doesn't contain uppercase letters", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun create_player_with_password_without_lowerscase_letters() {
        val playerInput = UserInput(
            "aleixo",
            "aleixo@casapia.pt",
            "CASAPIA6"
        )
        val exception = assertThrows<AppException> {
            services.createUser(playerInput)
        }
        Assertions.assertEquals("Password doesn't contain lowercase letters", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun get_player_info() {
        val token = "123"
        val expectedPlayer = User(1, "Leki", 420)
        val actualPlayer = services.getPlayerInfo(token)
        Assertions.assertEquals(expectedPlayer, actualPlayer)
    }

    @Test
    fun get_player_info_without_token() {
        val token = ""
        val exception = assertThrows<AppException> {
            services.getPlayerInfo(token)
        }
        Assertions.assertEquals("No token provided", exception.message)
        Assertions.assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun get_player_info_with_invalid_token() {
        val token = "nope"
        val exception = assertThrows<AppException> {
            services.getPlayerInfo(token)
        }
        Assertions.assertEquals("Invalid token", exception.message)
        Assertions.assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun get_rankings() {
        val rankings = services.getRankings(10, 0)
        Assertions.assertEquals(4, rankings.rankings.list[0].id)
        Assertions.assertEquals(3, rankings.rankings.list[1].id)
    }

    @Test
    fun get_rankings_with_invalid_limit() {
        val exception = assertThrows<AppException> {
            services.getRankings(-1, 0)
        }
        Assertions.assertEquals("Invalid limit", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun get_rankings_with_invalid_skip() {
        val exception = assertThrows<AppException> {
            services.getRankings(10, -1)
        }
        Assertions.assertEquals("Invalid skip", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun enter_lobby() {
        val player = User(3, "LordFarquaad", 510)
        val enterLobbyInput = EnterLobbyInput("beginner")
        val expectedOutput = EnterLobbyOutput(false, data.mockData.games.size + 1)
        val actualOutput = services.enterLobby(player, enterLobbyInput)
        Assertions.assertEquals(expectedOutput, actualOutput)
    }
}