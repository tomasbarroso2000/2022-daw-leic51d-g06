package pt.isel.leic.daw.explodingbattleships.server.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.leic.daw.explodingbattleships.domain.Fleet
import pt.isel.leic.daw.explodingbattleships.domain.Hits
import pt.isel.leic.daw.explodingbattleships.domain.Layout
import pt.isel.leic.daw.explodingbattleships.server.CREATED
import pt.isel.leic.daw.explodingbattleships.server.OK
import pt.isel.leic.daw.explodingbattleships.server.doApiTask
import pt.isel.leic.daw.explodingbattleships.server.token
import pt.isel.leic.daw.explodingbattleships.services.Services
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

const val SEND_HITS = "games/hit"
const val DEFINE_LAYOUT = "games/layout"
const val PLAYER_FLEET_STATE = "games/fleet/player/{gameId}"
const val ENEMY_FLEET_STATE = "games/fleet/enemy/{gameId}"

@RestController
@RequestMapping(BASE_PATH)
class InGameApi(private val services: Services) {

    @PostMapping(DEFINE_LAYOUT)
    fun defineLayout(
        request: HttpServletRequest,
        @Valid @RequestBody input: Layout,
    ) = doApiTask {
        val token = request.token
        ResponseEntity
            .status(CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.inGameServices.defineLayout(token, input))
    }

    @PostMapping(SEND_HITS)
    fun sendHits(
        request: HttpServletRequest,
        @Valid @RequestBody input: Hits,
    ) = doApiTask {
        val token = request.token
        ResponseEntity
            .status(CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.inGameServices.sendHits(token, input))
    }

    @GetMapping(PLAYER_FLEET_STATE)
    fun playerFleetState(
        request: HttpServletRequest,
        @PathVariable gameId: Int,
    ) = doApiTask {
        val token = request.token
        val input = Fleet(gameId, true)
        ResponseEntity
            .status(OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.inGameServices.fleetState(token, input))
    }

    @GetMapping(ENEMY_FLEET_STATE)
    fun enemyFleetState(
        request: HttpServletRequest,
        @PathVariable gameId: Int,
    ) = doApiTask {
        val token = request.token
        val input = Fleet(gameId, false)
        ResponseEntity
            .status(OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.inGameServices.fleetState(token, input))
    }
}