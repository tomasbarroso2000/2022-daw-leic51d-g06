package pt.isel.leic.daw.explodingbattleships.http

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersControllerTests {
    @LocalServerPort
    var port: Int = 0

    @Test
    fun can_get_player_home() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.get().uri("me")
            .header("Authorization", "Bearer 123")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun fails_get_player_home_without_token() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.get().uri("me")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun can_create_user() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        val name = "Fiona"
        val email = "iloveshrek@gmail.com"
        val password = "Shrekinho1"

        client.post().uri("users")
            .bodyValue(
                mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
    }

    @Test
    fun fails_create_user_without_body() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.post().uri("users")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun can_create_token() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        val email = "iloveshrek@gmail.com"
        val password = "Shrekinho1"

        client.post().uri("users/token")
            .bodyValue(
                mapOf(
                    "email" to email,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
    }

    @Test
    fun fails_create_token_without_body() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.post().uri("users/token")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun can_get_rankings() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.get().uri("users/rankings")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun can_enter_lobby() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        val gameType = "beginner"

        client.post().uri("lobby")
            .bodyValue(
                mapOf(
                    "game-type" to gameType
                )
            )
            .header("Authorization", "Bearer 123")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun fails_enter_lobby_without_body() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.post().uri("lobby")
            .header("Authorization", "Bearer 123")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun entered_game() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()

        client.put().uri("lobby/1")
            .header("Authorization", "Bearer 123")
            .exchange()
            .expectStatus().isOk
    }
}