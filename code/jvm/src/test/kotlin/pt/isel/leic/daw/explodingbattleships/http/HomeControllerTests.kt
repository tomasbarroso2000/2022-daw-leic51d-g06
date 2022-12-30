package pt.isel.leic.daw.explodingbattleships.http

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HomeControllerTests {
    @LocalServerPort
    var port: Int = 0

    @Test
    fun get_home() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api/").build()
        client.get().uri("")
            .exchange()
            .expectStatus().isOk
    }
}