package pt.isel.leic.daw.explodingbattleships.http

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.leic.daw.explodingbattleships.domain.Square

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GamesControllerTests {
    @LocalServerPort
    var port: Int = 0

    @Test
    fun can_get_game_info() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()
        client.get().uri("games/info/1")
            .header("Authorization", "Bearer 123")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun fails_get_game_info_with_invalid_game_id() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()
        client.get().uri("games/info/abc")
            .header("Authorization", "Bearer 123")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun can_get_number_of_played_games() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()
        client.get().uri("games/total")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun can_get_game_state() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.get().uri("games/state/1")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun fails_get_game_state_with_invalid_game_id() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.get().uri("games/state/abc")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun can_get_player_fleet_state() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()
        client.get().uri("games/fleet/player/1")
            .header("Authorization", "Bearer 123")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun fails_get_player_fleet_state_without_token() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.get().uri("games/fleet/player/1")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun can_get_enemy_fleet_state() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.get().uri("games/fleet/enemy/1")
            .header("Authorization", "Bearer 123")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun fails_get_enemy_fleet_state_without_token() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.get().uri("games/fleet/enemy/1")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun can_send_hits() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        val gameId = 2
        val squares = listOf(
            Square('a', 1),
            Square('b', 1)
        )

        client.put().uri("games/hit")
            .bodyValue(
                mapOf(
                    "game-id" to gameId,
                    "squares" to squares
                )
            )
            .header("Authorization", "Bearer 123")
            .exchange()
            .expectStatus().isCreated
    }

    @Test
    fun fails_send_hits_without_body() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.put().uri("games/hit")
            .header("Authorization", "Bearer 123")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun fails_define_layout_without_body() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.put().uri("games/layout")
            .header("Authorization", "Bearer 123")
            .exchange()
            .expectStatus().isBadRequest
    }
}