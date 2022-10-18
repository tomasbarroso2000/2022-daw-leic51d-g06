package pt.isel.leic.daw.explodingbattleships.http.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.daw.explodingbattleships.domain.PlayerInput
import pt.isel.leic.daw.explodingbattleships.http.*
import pt.isel.leic.daw.explodingbattleships.http.Uris.BASE_PATH
import pt.isel.leic.daw.explodingbattleships.http.Uris.CREATE_PLAYER
import pt.isel.leic.daw.explodingbattleships.http.Uris.GAME_STATE
import pt.isel.leic.daw.explodingbattleships.http.Uris.NUMBER_OF_PLAYED_GAMES
import pt.isel.leic.daw.explodingbattleships.http.Uris.RANKINGS
import pt.isel.leic.daw.explodingbattleships.infra.siren
import pt.isel.leic.daw.explodingbattleships.services.Services
import javax.validation.Valid

/*const val BASE_PATH = "/battleships/"

const val CREATE_PLAYER = "players"
const val RANKINGS = "rankings"
const val NUMBER_OF_PLAYED_GAMES = "games/total"
const val GAME_STATE = "games/state/{gameId}"*/

@RestController
@RequestMapping(BASE_PATH)
class UnauthenticatedApi(private val services: Services) {

    @PostMapping(CREATE_PLAYER)
    fun handlerCreatePlayer(
        @Valid @RequestBody input: PlayerInput
    ) = doApiTask {
        ResponseEntity
            .status(CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.unauthenticatedServices.createPlayer(input))
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

    @GetMapping(RANKINGS)
    fun handlerRankings(
        @RequestParam(required = false, defaultValue = "10") limit: Int,
        @RequestParam(required = false, defaultValue = "0") skip: Int = 0,
    ) = doApiTask {
        ResponseEntity
            .status(OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.unauthenticatedServices.getRankings(limit, skip))
    }

    @GetMapping
    fun handlerSystemInfo() = doApiTask {
        ResponseEntity
            .status(OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(services.unauthenticatedServices.getSystemInfo()){
                    link(Uris.home(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                }
            )
    }
}
