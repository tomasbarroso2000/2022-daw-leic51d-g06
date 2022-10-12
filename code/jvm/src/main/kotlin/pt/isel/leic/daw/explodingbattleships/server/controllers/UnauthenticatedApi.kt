package pt.isel.leic.daw.explodingbattleships.server.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.daw.explodingbattleships.server.OK
import pt.isel.leic.daw.explodingbattleships.server.doApiTask
import pt.isel.leic.daw.explodingbattleships.services.Services

const val BASE_PATH = "/battleships/"
const val RANKINGS = "rankings"
const val NUMBER_OF_PLAYED_GAMES = "games/total"
const val GAME_STATE = "games/state/{gameId}"

@RestController
@RequestMapping(BASE_PATH)
class UnauthenticatedApi(private val services: Services) {

    @GetMapping(RANKINGS)
    fun handlerRankings(
        @RequestParam limit: Int,
        @RequestParam skip: Int,
    ) = doApiTask {
        ResponseEntity
            .status(OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.unauthenticatedServices.getRankings(limit, skip))
    }

    @GetMapping(NUMBER_OF_PLAYED_GAMES)
    fun handlerNrOfPlayedGames() =
        doApiTask {
            ResponseEntity
                .status(OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(services.unauthenticatedServices.getNumberOfPlayedGames())
        }

    @GetMapping(GAME_STATE)
    fun handlerGameState(@PathVariable gameId: Int) =
        doApiTask { ResponseEntity
            .status(OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.unauthenticatedServices.getGameState(gameId))
    }
}
