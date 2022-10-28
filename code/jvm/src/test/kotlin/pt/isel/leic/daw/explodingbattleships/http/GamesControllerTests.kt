package pt.isel.leic.daw.explodingbattleships.http

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient

/**
 *  Only simple tests for now.
 */
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
    fun can_get_player_fleet_state() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.get().uri("games/fleet/player/1")
            .header("Authorization", "Bearer 123")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun can_get_enemy_fleet_state() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.get().uri("games/fleet/enemy/1")
            .header("Authorization", "Bearer 123")
            .exchange()
            .expectStatus().isOk
    }
}