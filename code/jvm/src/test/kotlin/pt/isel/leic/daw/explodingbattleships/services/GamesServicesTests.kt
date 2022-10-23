package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.mem.DataMem
import pt.isel.leic.daw.explodingbattleships.domain.*
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus

class GamesServicesTests {
    private val data = DataMem()
    private val services = GamesServices(data)

    @Test
    fun get_number_of_played_games() {
        val number = services.getNumberOfPlayedGames()
        Assertions.assertEquals(data.mockData.games.size, number)
    }

    @Test
    fun get_game_state() {
        val state = services.getGameState(1)
        Assertions.assertEquals("layout_definition", state)
    }

    @Test
    fun get_game_state_of_invalid_game() {
        val exception = assertThrows<AppException> {
            services.getGameState(-1)
        }
        Assertions.assertEquals("Invalid gameId", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun get_game_state_of_non_existing_game() {
        val exception = assertThrows<AppException> {
            services.getGameState(100)
        }
        Assertions.assertEquals("Game does not exist", exception.message)
        Assertions.assertEquals(AppExceptionStatus.NOT_FOUND, exception.status)
    }


    @Test
    fun define_layout() {
        val userId = 1
        val layout = Layout(
            3,
            listOf(
                UnverifiedShip("carrier", UnverifiedSquare('a', 1), "horizontal"),
                UnverifiedShip("battleship", UnverifiedSquare('b', 1), "vertical"),
                UnverifiedShip("submarine", UnverifiedSquare('b', 2), "horizontal"),
                UnverifiedShip("cruiser", UnverifiedSquare('c', 2), "horizontal"),
                UnverifiedShip("destroyer", UnverifiedSquare('d', 2), "vertical")
            )
        )
        val expectedLayoutOutcome = LayoutOutcome(LayoutOutcomeStatus.WAITING)
        val actualLayoutOutcome = services.sendLayout(userId, layout.gameId, layout.ships)
        Assertions.assertEquals(expectedLayoutOutcome, actualLayoutOutcome)
        Assertions.assertEquals(5, data.mockData.ships.filter { it.game == 3 && it.player == 1 }.size)
    }

    @Test
    fun define_layout_with_player_not_in_a_game() {
        val userId = 1
        val layout = Layout(
            2,
            listOf(
                UnverifiedShip("carrier", UnverifiedSquare('a', 1), "horizontal"),
                UnverifiedShip("battleship", UnverifiedSquare('b', 1), "vertical"),
                UnverifiedShip("submarine", UnverifiedSquare('b', 2), "horizontal"),
                UnverifiedShip("cruiser", UnverifiedSquare('c', 2), "horizontal"),
                UnverifiedShip("destroyer", UnverifiedSquare('d', 2), "vertical")
            )
        )
        val exception = assertThrows<AppException> {
            services.sendLayout(userId, layout.gameId, layout.ships)
        }
        Assertions.assertEquals("Player not in game", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun define_layout_with_invalid_orientation() {
        val userId = 1
        val layout = Layout(
            3,
            listOf(
                UnverifiedShip("carrier", UnverifiedSquare('a', 1), "horizontal"),
                UnverifiedShip("battleship", UnverifiedSquare('b', 1), "vertical"),
                UnverifiedShip("submarine", UnverifiedSquare('b', 2), "up"),
                UnverifiedShip("cruiser", UnverifiedSquare('c', 2), "horizontal"),
                UnverifiedShip("destroyer", UnverifiedSquare('d', 2), "vertical")
            )
        )
        val exception = assertThrows<AppException> {
            services.sendLayout(userId, layout.gameId, layout.ships)
        }
        Assertions.assertEquals("Invalid orientation for submarine", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun define_layout_with_invalid_ship() {
        val userId = 1
        val layout = Layout(
            3,
            listOf(
                UnverifiedShip("carrier", UnverifiedSquare('a', 1), "horizontal"),
                UnverifiedShip("battleship", UnverifiedSquare('b', 1), "vertical"),
                UnverifiedShip("submarine", UnverifiedSquare('b', 2), "horizontal"),
                UnverifiedShip("smoothie", UnverifiedSquare('c', 2), "horizontal"),
                UnverifiedShip("destroyer", UnverifiedSquare('d', 2), "vertical")
            )
        )
        val exception = assertThrows<AppException> {
            services.sendLayout(userId, layout.gameId, layout.ships)
        }
        Assertions.assertEquals("Invalid ship list for BEGINNER game", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun define_layout_of_layout_already_defined() {
        val userId = 1
        val layout = Layout(
            1,
            listOf(
                UnverifiedShip("carrier", UnverifiedSquare('a', 1), "horizontal"),
                UnverifiedShip("battleship", UnverifiedSquare('b', 1), "vertical"),
                UnverifiedShip("submarine", UnverifiedSquare('b', 2), "horizontal"),
                UnverifiedShip("cruiser", UnverifiedSquare('c', 2), "horizontal"),
                UnverifiedShip("destroyer", UnverifiedSquare('d', 2), "vertical")
            )
        )
        val exception = assertThrows<AppException> {
            services.sendLayout(userId, layout.gameId, layout.ships)
        }
        Assertions.assertEquals("Layout already defined", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits() {
        val userId = 5
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare('e', 2)
            )
        )
        val expectedHitsOutcome = HitsOutcome(
            listOf(
                HitOutcome(VerifiedSquare('d', 2), true),
                HitOutcome(VerifiedSquare('e', 2), true, "destroyer")
            ),
            false
        )
        val actualHitsOutcome = services.sendHits(userId, hits.gameId, hits.squares)
        Assertions.assertEquals(expectedHitsOutcome, actualHitsOutcome)
        Assertions.assertEquals(3, data.mockData.hits.filter { it.game == 2 && it.player == 6 }.size)
    }

    @Test
    fun send_hits_with_player_not_in_a_game() {
        val useriD = 3
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare('e', 2)
            )
        )
        val exception = assertThrows<AppException> {
            services.sendHits(useriD, hits.gameId, hits.squares)
        }
        Assertions.assertEquals("Player not in game", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_player_not_current() {
        val userId = 6
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare('e', 2)
            )
        )
        val exception = assertThrows<AppException> {
            services.sendHits(userId, hits.gameId, hits.squares)
        }
        Assertions.assertEquals("Not your turn", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_no_squares() {
        val userId = 5
        val hits = Hits(2, listOf())
        val exception = assertThrows<AppException> {
            services.sendHits(userId, hits.gameId, hits.squares)
        }
        Assertions.assertEquals("No squares provided", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_invalid_amount_of_hits() {
        val userId = 5
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare('e', 2),
                UnverifiedSquare('e', 3),
                UnverifiedSquare('e', 4),
                UnverifiedSquare('e', 5),
                UnverifiedSquare('e', 6)
            )
        )
        val exception = assertThrows<AppException> {
            services.sendHits(userId, hits.gameId, hits.squares)
        }
        Assertions.assertEquals("Invalid amount of hits", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_invalid_square() {
        val userId = 5
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare(null, 2)
            )
        )
        val exception = assertThrows<AppException> {
            services.sendHits(userId, hits.gameId, hits.squares)
        }
        Assertions.assertEquals("Invalid square: null2", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_square_not_in_board() {
        val userId = 5
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare('z', 2)
            )
        )
        val exception = assertThrows<AppException> {
            services.sendHits(userId, hits.gameId, hits.squares)
        }
        Assertions.assertEquals("Invalid square: z2", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_square_already_hit() {
        val userId = 5
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare('f', 1)
            )
        )
        val exception = assertThrows<AppException> {
            services.sendHits(userId, hits.gameId, hits.squares)
        }
        Assertions.assertEquals("Square already hit: f1", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_and_win_the_game() {
        val userId = 3
        val hits = Hits(
            4,
            listOf(
                UnverifiedSquare('a', 1),
                UnverifiedSquare('b', 1)
            )
        )
        val expectedHitsOutcome = HitsOutcome(
            listOf(
                HitOutcome(VerifiedSquare('a', 1), true, null),
                HitOutcome(VerifiedSquare('b', 1), true, "destroyer")
            ),
            true
        )
        val actualHitsOutcome = services.sendHits(userId, hits.gameId, hits.squares)
        Assertions.assertEquals(expectedHitsOutcome, actualHitsOutcome)
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
        Assertions.assertEquals(expectedFleet, actualFleet)
    }

    @Test
    fun player_fleet_state_with_player_not_in_a_game() {
        val userId = 3
        val exception = assertThrows<AppException> {
            services.fleetState(userId, 1, true)
        }
        Assertions.assertEquals("Player not in game", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
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
        val actualFleet = services.fleetState(userId,1, false)
        Assertions.assertEquals(expectedFleet, actualFleet)
    }

    @Test
    fun enemy_fleet_state_with_player_not_in_a_game() {
        val userId = 3
        val exception = assertThrows<AppException> {
            services.fleetState(userId,1, false)
        }
        Assertions.assertEquals("Player not in game", exception.message)
        Assertions.assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }
}