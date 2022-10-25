package pt.isel.leic.daw.explodingbattleships.http

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

/**
 *  Only simple tests for now.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GamesControllerTests {
    @LocalServerPort
    var port: Int = 0
/*
    @Test
    fun can_get_number_of_played_games() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client.get().uri(Uris.Games.nrOfGames())
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun can_get_game_state() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client.get().uri(Uris.Games.state(1))
            .exchange()
            .expectStatus().isOk
    }

 */
}
