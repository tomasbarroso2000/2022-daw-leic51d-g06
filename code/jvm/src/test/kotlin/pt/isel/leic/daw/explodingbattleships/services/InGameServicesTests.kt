package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.mem.DataMem
import pt.isel.leic.daw.explodingbattleships.domain.*
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus

class InGameServicesTests {
    private val data = DataMem()
    private val services = Services(data).inGameServices

    @Test
    fun define_layout() {
        val token = "123"
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
        val expectedLayoutOutcome = LayoutOutcome(LayoutOutcomeStatus.STARTED)
        val actualLayoutOutcome = services.defineLayout(token, layout)
        assertEquals(expectedLayoutOutcome, actualLayoutOutcome)
    }

    @Test
    fun define_layout_without_token() {
        val token = ""
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
            services.defineLayout(token, layout)
        }
        assertEquals("No token provided", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun define_layout_with_invalid_token() {
        val token = "nope"
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
            services.defineLayout(token, layout)
        }
        assertEquals("Invalid token", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun define_layout_with_player_not_in_a_game() {
        val token = "123"
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
            services.defineLayout(token, layout)
        }
        assertEquals("Player not in game", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun define_layout_with_invalid_orientation() {
        val token = "123"
        val layout = Layout(
            1,
            listOf(
                UnverifiedShip("carrier", UnverifiedSquare('a', 1), "horizontal"),
                UnverifiedShip("battleship", UnverifiedSquare('b', 1), "vertical"),
                UnverifiedShip("submarine", UnverifiedSquare('b', 2), "up"),
                UnverifiedShip("cruiser", UnverifiedSquare('c', 2), "horizontal"),
                UnverifiedShip("destroyer", UnverifiedSquare('d', 2), "vertical")
            )
        )
        val exception = assertThrows<AppException> {
            services.defineLayout(token, layout)
        }
        assertEquals("Invalid orientation for submarine", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun define_layout_with_invalid_ship() {
        val token = "123"
        val layout = Layout(
            1,
            listOf(
                UnverifiedShip("carrier", UnverifiedSquare('a', 1), "horizontal"),
                UnverifiedShip("battleship", UnverifiedSquare('b', 1), "vertical"),
                UnverifiedShip("submarine", UnverifiedSquare('b', 2), "horizontal"),
                UnverifiedShip("smoothie", UnverifiedSquare('c', 2), "horizontal"),
                UnverifiedShip("destroyer", UnverifiedSquare('d', 2), "vertical")
            )
        )
        val exception = assertThrows<AppException> {
            services.defineLayout(token, layout)
        }
        assertEquals("Invalid ship list for BEGINNER game", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits() {
        val token = "buro"
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare('e', 2)
            )
        )
        val expectedHitsOutcome = listOf(
            HitOutcome(VerifiedSquare('d', 2), true),
            HitOutcome(VerifiedSquare('e', 2), true, "destroyer")
        )
        val actualHitsOutcome = services.sendHits(token, hits)
        assertEquals(expectedHitsOutcome, actualHitsOutcome)
    }

    @Test
    fun send_hits_without_token() {
        val token = ""
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare('e', 2)
            )
        )
        val exception = assertThrows<AppException> {
            services.sendHits(token, hits)
        }
        assertEquals("No token provided", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun send_hits_with_invalid_token() {
        val token = "nope"
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare('e', 2)
            )
        )
        val exception = assertThrows<AppException> {
            services.sendHits(token, hits)
        }
        assertEquals("Invalid token", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun send_hits_with_player_not_in_a_game() {
        val token = "fiona"
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare('e', 2)
            )
        )
        val exception = assertThrows<AppException> {
            services.sendHits(token, hits)
        }
        assertEquals("Player not in game", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_player_not_current() {
        val token = "shrekinho"
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare('e', 2)
            )
        )
        val exception = assertThrows<AppException> {
            services.sendHits(token, hits)
        }
        assertEquals("Not your turn", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_no_squares() {
        val token = "buro"
        val hits = Hits(2, listOf())
        val exception = assertThrows<AppException> {
            services.sendHits(token, hits)
        }
        assertEquals("No squares provided", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_invalid_amount_of_hits() {
        val token = "buro"
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
            services.sendHits(token, hits)
        }
        assertEquals("Invalid amount of hits", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_invalid_square() {
        val token = "buro"
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare(null, 2)
            )
        )
        val exception = assertThrows<AppException> {
            services.sendHits(token, hits)
        }
        assertEquals("Invalid square: null2", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_square_not_in_board() {
        val token = "buro"
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare('z', 2)
            )
        )
        val exception = assertThrows<AppException> {
            services.sendHits(token, hits)
        }
        assertEquals("Invalid square: z2", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun send_hits_with_square_already_hit() {
        val token = "buro"
        val hits = Hits(
            2,
            listOf(
                UnverifiedSquare('d', 2),
                UnverifiedSquare('f', 1)
            )
        )
        val exception = assertThrows<AppException> {
            services.sendHits(token, hits)
        }
        assertEquals("Square already hit: f1", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun player_fleet_state() {
        val token = "123"
        val expectedFleet = listOf(
            ShipState("carrier", false),
            ShipState("battleship", false),
            ShipState("cruiser", false),
            ShipState("submarine", false),
            ShipState("destroyer", false)
        )
        val actualFleet = services.fleetState(token, Fleet(1, true))
        assertEquals(expectedFleet, actualFleet)
    }

    @Test
    fun player_fleet_state_without_token() {
        val token = ""
        val exception = assertThrows<AppException> {
            services.fleetState(token, Fleet(1, true))
        }
        assertEquals("No token provided", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun player_fleet_state_with_invalid_token() {
        val token = "nope"
        val exception = assertThrows<AppException> {
            services.fleetState(token, Fleet(1, true))
        }
        assertEquals("Invalid token", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun player_fleet_state_with_player_not_in_a_game() {
        val token = "fiona"
        val exception = assertThrows<AppException> {
            services.fleetState(token, Fleet(1, true))
        }
        assertEquals("Player not in game", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun enemy_fleet_state() {
        val token = "123"
        val expectedFleet = listOf(
            ShipState("carrier", false),
            ShipState("battleship", false),
            ShipState("cruiser", false),
            ShipState("submarine", true),
            ShipState("destroyer", true)
        )
        val actualFleet = services.fleetState(token, Fleet(1, false))
        assertEquals(expectedFleet, actualFleet)
    }

    @Test
    fun enemy_fleet_state_without_token() {
        val token = ""
        val exception = assertThrows<AppException> {
            services.fleetState(token, Fleet(1, false))
        }
        assertEquals("No token provided", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun enemy_fleet_state_with_invalid_token() {
        val token = "nope"
        val exception = assertThrows<AppException> {
            services.fleetState(token, Fleet(1, false))
        }
        assertEquals("Invalid token", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun enemy_fleet_state_with_player_not_in_a_game() {
        val token = "fiona"
        val exception = assertThrows<AppException> {
            services.fleetState(token, Fleet(1, false))
        }
        assertEquals("Player not in game", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }
}
