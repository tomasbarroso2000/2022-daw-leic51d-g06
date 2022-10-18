package pt.isel.leic.daw.explodingbattleships.http.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.leic.daw.explodingbattleships.domain.Fleet
import pt.isel.leic.daw.explodingbattleships.domain.Hits
import pt.isel.leic.daw.explodingbattleships.domain.Layout
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.http.CREATED
import pt.isel.leic.daw.explodingbattleships.http.OK
import pt.isel.leic.daw.explodingbattleships.http.Uris.BASE_PATH
import pt.isel.leic.daw.explodingbattleships.http.doApiTask
import pt.isel.leic.daw.explodingbattleships.http.token
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
    fun handlerDefineLayout(
        player: Player,
        @Valid @RequestBody input: Layout,
    ) = doApiTask {
        ResponseEntity
            .status(CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.inGameServices.defineLayout(player, input))
    }

    @PostMapping(SEND_HITS)
    fun handlerSendHits(
        player: Player,
        @Valid @RequestBody input: Hits,
    ) = doApiTask {
        ResponseEntity
            .status(CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.inGameServices.sendHits(player, input))
    }

    @GetMapping(PLAYER_FLEET_STATE)
    fun handlePlayerFleetState(
        player: Player,
        @PathVariable gameId: Int,
    ) = doApiTask {
        val input = Fleet(gameId, true)
        ResponseEntity
            .status(OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.inGameServices.fleetState(player, input))
    }

    @GetMapping(ENEMY_FLEET_STATE)
    fun handlerEnemyFleetState(
        player: Player,
        @PathVariable gameId: Int,
    ) = doApiTask {
        val input = Fleet(gameId, false)
        ResponseEntity
            .status(OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.inGameServices.fleetState(player, input))
    }
}