package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.mem.DataMem
import pt.isel.leic.daw.explodingbattleships.domain.HitOutcome
import pt.isel.leic.daw.explodingbattleships.domain.HitsOutcome
import pt.isel.leic.daw.explodingbattleships.domain.LayoutOutcomeStatus
import pt.isel.leic.daw.explodingbattleships.domain.ShipCreationInfo
import pt.isel.leic.daw.explodingbattleships.domain.ShipState
import pt.isel.leic.daw.explodingbattleships.domain.Square
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus

class GamesServicesTests {
    private val data = DataMem()
    private val services = GamesServices(data)

    @Test
    fun get_game() {
        val outcome = services.getGame(1, 1)
        assertEquals(1, outcome.game.id)
        assertTrue(outcome.playing)
        assertEquals(2, outcome.opponent)
    }

    @Test
    fun get_game_with_invalid_game() {
        val exception = assertThrows<AppException> {
            services.getGame(1, -1)
        }
        assertEquals("Invalid game id", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun get_number_of_played_games() {
        val number = services.getNumberOfPlayedGames()
        assertEquals(data.mockData.games.size, number)
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
    fun define_layout_waiting() {
        val userId = 1
        val gameId = 3
        val ships = listOf(
            ShipCreationInfo("carrier", Square('a', 1), "horizontal"),
            ShipCreationInfo("battleship", Square('b', 1), "vertical"),
            ShipCreationInfo("submarine", Square('b', 2), "horizontal"),
            ShipCreationInfo("cruiser", Square('c', 2), "horizontal"),
            ShipCreationInfo("destroyer", Square('d', 2), "vertical")
        )
        val expectedLayoutOutcome = LayoutOutcomeStatus.WAITING
        val actualLayoutOutcome = services.sendLayout(userId, gameId, ships)
        assertEquals(expectedLayoutOutcome, actualLayoutOutcome)
        assertEquals(5, data.mockData.ships.filter { it.gameId == gameId && it.userId == userId }.size)
    }

    @Test
    fun define_layout_started() {
        val userId = 7
        val gameId = 5
        val ships = listOf(
            ShipCreationInfo("carrier", Square('a', 1), "horizontal"),
            ShipCreationInfo("battleship", Square('b', 1), "vertical"),
            ShipCreationInfo("submarine", Square('b', 2), "horizontal"),
            ShipCreationInfo("cruiser", Square('c', 2), "horizontal"),
            ShipCreationInfo("destroyer", Square('d', 2), "vertical")
        )
        val expectedLayoutOutcome = LayoutOutcomeStatus.STARTED
        val actualLayoutOutcome = services.sendLayout(userId, gameId, ships)
        assertEquals(expectedLayoutOutcome, actualLayoutOutcome)
        assertEquals(5, data.mockData.ships.filter { it.gameId == gameId && it.userId == userId }.size)
    }

    @Test
    fun define_layout_with_player_not_in_a_game() {
        val userId = 1
        val gameId = 2
        val ships = listOf(
            ShipCreationInfo("carrier", Square('a', 1), "horizontal"),
            ShipCreationInfo("battleship", Square('b', 1), "vertical"),
            ShipCreationInfo("submarine", Square('b', 2), "horizontal"),
            ShipCreationInfo("cruiser", Square('c', 2), "horizontal"),
            ShipCreationInfo("destroyer", Square('d', 2), "vertical")
        )
        val exception = assertThrows<AppException> {
            services.sendLayout(userId, gameId, ships)
        }
        assertEquals("Player not in game", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun define_layout_with_invalid_orientation() {
        val userId = 1
        val gameId = 3
        val ships = listOf(
            ShipCreationInfo("carrier", Square('a', 1), "horizontal"),
            ShipCreationInfo("battleship", Square('b', 1), "vertical"),
            ShipCreationInfo("submarine", Square('b', 2), "up"),
            ShipCreationInfo("cruiser", Square('c', 2), "horizontal"),
            ShipCreationInfo("destroyer", Square('d', 2), "vertical")
        )
        val exception = assertThrows<AppException> {
            services.sendLayout(userId, gameId, ships)
        }
        assertEquals("Invalid orientation for submarine", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun define_layout_with_invalid_ship() {
        val userId = 1
        val gameId = 3
        val ships = listOf(
            ShipCreationInfo("carrier", Square('a', 1), "horizontal"),
            ShipCreationInfo("battleship", Square('b', 1), "vertical"),
            ShipCreationInfo("submarine", Square('b', 2), "horizontal"),
            ShipCreationInfo("smoothie", Square('c', 2), "horizontal"),
            ShipCreationInfo("destroyer", Square('d', 2), "vertical")
        )
        val exception = assertThrows<AppException> {
            services.sendLayout(userId, gameId, ships)
        }
        assertEquals("Invalid ship list for BEGINNER game", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun define_layout_of_layout_already_defined() {
        val userId = 1
        val gameId = 1
        val ships = listOf(
            ShipCreationInfo("carrier", Square('a', 1), "horizontal"),
            ShipCreationInfo("battleship", Square('b', 1), "vertical"),
            ShipCreationInfo("submarine", Square('b', 2), "horizontal"),
            ShipCreationInfo("cruiser", Square('c', 2), "horizontal"),
            ShipCreationInfo("destroyer", Square('d', 2), "vertical")
        )
        val exception = assertThrows<AppException> {
            services.sendLayout(userId, gameId, ships)
        }
        assertEquals("Layout already defined", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits() {
        val userId = 5
        val gameId = 2
        val expectedHitsOutcome = HitsOutcome(
            listOf(
                HitOutcome(Square('d', 2), true),
                HitOutcome(Square('e', 2), true, "destroyer")
            ),
            false
        )
        val actualHitsOutcome = services.sendHits(
            userId,
            gameId,
            listOf(
                Square('d', 2),
                Square('e', 2)
            )
        )
        assertEquals(expectedHitsOutcome, actualHitsOutcome)
        assertEquals(3, data.mockData.hits.filter { it.gameId == 2 && it.userId == 6 }.size)
    }

    @Test
    fun send_hits_with_player_not_in_a_game() {
        val userId = 3
        val gameId = 2
        val squares = listOf(
            Square('d', 2),
            Square('e', 2)
        )
        val exception = assertThrows<AppException> {
            services.sendHits(userId, gameId, squares)
        }
        assertEquals("Player not in game", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_player_not_current() {
        val userId = 6
        val gameId = 2
        val squares = listOf(
            Square('d', 2),
            Square('e', 2)
        )
        val exception = assertThrows<AppException> {
            services.sendHits(userId, gameId, squares)
        }
        assertEquals("Not your turn", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_no_squares() {
        val userId = 5
        val gameId = 2
        val squares = listOf<Square>()
        val exception = assertThrows<AppException> {
            services.sendHits(userId, gameId, squares)
        }
        assertEquals("No squares provided", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_invalid_amount_of_hits() {
        val userId = 5
        val gameId = 2
        val squares = listOf(
            Square('d', 2),
            Square('e', 2),
            Square('e', 3),
            Square('e', 4),
            Square('e', 5),
            Square('e', 6)
        )
        val exception = assertThrows<AppException> {
            services.sendHits(userId, gameId, squares)
        }
        assertEquals("Invalid amount of hits", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_square_not_in_board() {
        val userId = 5
        val gameId = 2
        val squares = listOf(
            Square('d', 2),
            Square('z', 2)
        )
        val exception = assertThrows<AppException> {
            services.sendHits(userId, gameId, squares)
        }
        assertEquals("Invalid square: z2", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_square_already_hit() {
        val userId = 5
        val gameId = 2
        val squares = listOf(
            Square('d', 2),
            Square('f', 1)
        )
        val exception = assertThrows<AppException> {
            services.sendHits(userId, gameId, squares)
        }
        assertEquals("Square already hit: f1", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_and_win_the_game() {
        val userId = 3
        val gameId = 4
        val squares = listOf(
            Square('a', 1),
            Square('b', 1)
        )
        val expectedHitsOutcome = HitsOutcome(
            listOf(
                HitOutcome(Square('a', 1), true, null),
                HitOutcome(Square('b', 1), true, "destroyer")
            ),
            true
        )
        val actualHitsOutcome = services.sendHits(userId, gameId, squares)
        assertEquals(expectedHitsOutcome, actualHitsOutcome)
    }

    @Test
    fun player_fleet_state() {
        val userId = 1
        val expectedFleet = listOf(
            ShipState("carrier", false),
            ShipState("battleship", false),
            ShipState("cruiser", false),
            ShipState("submarine", false),
            ShipState("destroyer", false)
        )
        val actualFleet = services.fleetState(userId, 1, true)
        assertEquals(expectedFleet, actualFleet)
    }

    @Test
    fun player_fleet_state_with_player_not_in_a_game() {
        val userId = 3
        val exception = assertThrows<AppException> {
            services.fleetState(userId, 1, true)
        }
        assertEquals("Player not in game", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun enemy_fleet_state() {
        val userId = 1
        val expectedFleet = listOf(
            ShipState("carrier", false),
            ShipState("battleship", false),
            ShipState("cruiser", false),
            ShipState("submarine", true),
            ShipState("destroyer", true)
        )
        val actualFleet = services.fleetState(userId, 1, false)
        assertEquals(expectedFleet, actualFleet)
    }

    @Test
    fun enemy_fleet_state_with_player_not_in_a_game() {
        val userId = 3
        val exception = assertThrows<AppException> {
            services.fleetState(userId, 1, false)
        }
        assertEquals("Player not in game", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }
}
