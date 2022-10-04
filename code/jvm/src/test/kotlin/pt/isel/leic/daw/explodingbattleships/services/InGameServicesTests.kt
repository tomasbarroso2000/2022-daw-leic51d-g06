package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.DataMem
import pt.isel.leic.daw.explodingbattleships.domain.Layout
import pt.isel.leic.daw.explodingbattleships.domain.ShipState
import pt.isel.leic.daw.explodingbattleships.domain.UnverifiedShip
import pt.isel.leic.daw.explodingbattleships.domain.UnverifiedSquare
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

    // Send hits tests

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
