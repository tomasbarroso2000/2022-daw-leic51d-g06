package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.DataMem
import pt.isel.leic.daw.explodingbattleships.domain.Layout
import pt.isel.leic.daw.explodingbattleships.domain.UnverifiedShip
import pt.isel.leic.daw.explodingbattleships.domain.UnverifiedSquare
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppException

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
    }
}
