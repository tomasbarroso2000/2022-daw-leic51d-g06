package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.DataMem
import pt.isel.leic.daw.explodingbattleships.domain.HitOutcome
import pt.isel.leic.daw.explodingbattleships.domain.Hits
import pt.isel.leic.daw.explodingbattleships.domain.Layout
import pt.isel.leic.daw.explodingbattleships.domain.ShipState
import pt.isel.leic.daw.explodingbattleships.domain.UnverifiedShip
import pt.isel.leic.daw.explodingbattleships.domain.UnverifiedSquare
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppExceptionStatus

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
        assertTrue(services.defineLayout(token, layout))
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
        assertEquals(exception.message, "Invalid ship list")
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
                UnverifiedSquare('e', 3)
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
        val actualFleet = services.playerFleetState(token)
        assertEquals(expectedFleet, actualFleet)
    }

    @Test
    fun player_fleet_state_without_token() {
        val token = ""
        val exception = assertThrows<AppException> {
            services.playerFleetState(token)
        }
        assertEquals("No token provided", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun player_fleet_state_with_invalid_token() {
        val token = "nope"
        val exception = assertThrows<AppException> {
            services.playerFleetState(token)
        }
        assertEquals("Invalid token", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun player_fleet_state_with_player_not_in_a_game() {
        val token = "fiona"
        val exception = assertThrows<AppException> {
            services.playerFleetState(token)
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
        val actualFleet = services.enemyFleetState(token)
        assertEquals(expectedFleet, actualFleet)
    }

    @Test
    fun enemy_fleet_state_without_token() {
        val token = ""
        val exception = assertThrows<AppException> {
            services.enemyFleetState(token)
        }
        assertEquals("No token provided", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun enemy_fleet_state_with_invalid_token() {
        val token = "nope"
        val exception = assertThrows<AppException> {
            services.enemyFleetState(token)
        }
        assertEquals("Invalid token", exception.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, exception.status)
    }

    @Test
    fun enemy_fleet_state_with_player_not_in_a_game() {
        val token = "fiona"
        val exception = assertThrows<AppException> {
            services.enemyFleetState(token)
        }
        assertEquals("Player not in game", exception.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, exception.status)
    }
}
