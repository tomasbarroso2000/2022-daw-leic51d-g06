package pt.isel.leic.daw.explodingbattleships.http.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.leic.daw.explodingbattleships.domain.Fleet
import pt.isel.leic.daw.explodingbattleships.domain.Hits
import pt.isel.leic.daw.explodingbattleships.domain.Layout
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.http.*
import pt.isel.leic.daw.explodingbattleships.http.Uris.BASE_PATH
import pt.isel.leic.daw.explodingbattleships.http.Uris.DEFINE_LAYOUT
import pt.isel.leic.daw.explodingbattleships.http.Uris.ENEMY_FLEET_STATE
import pt.isel.leic.daw.explodingbattleships.http.Uris.PLAYER_FLEET_STATE
import pt.isel.leic.daw.explodingbattleships.http.Uris.SEND_HITS
import pt.isel.leic.daw.explodingbattleships.infra.siren
import pt.isel.leic.daw.explodingbattleships.services.Services
import javax.validation.Valid

@RestController
@RequestMapping(BASE_PATH)
class InGameApi(private val services: Services) {

    @PostMapping(DEFINE_LAYOUT)
    fun handlerDefineLayout(
        player: Player,
        @Valid @RequestBody input: Layout,
    ) = doApiTask {
        ResponseEntity
            .status(Successes.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(services.inGameServices.sendLayout(player, input)) {
                    link(Uris.defineLayout(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                }
            )
    }

    @PostMapping(SEND_HITS)
    fun handlerSendHits(
        player: Player,
        @Valid @RequestBody input: Hits,
    ) = doApiTask {
        ResponseEntity
            .status(Successes.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(services.inGameServices.sendHits(player, input)) {
                    link(Uris.sendHits(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                }
            )
    }

    @GetMapping(PLAYER_FLEET_STATE)
    fun handlePlayerFleetState(
        player: Player,
        @PathVariable gameId: Int,
    ) = doApiTask {
        val input = Fleet(gameId, true)
        ResponseEntity
            .status(Successes.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(services.inGameServices.fleetState(player, input)) {
                    link(Uris.playerFleetState(gameId), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                }
            )
    }

    @GetMapping(ENEMY_FLEET_STATE)
    fun handlerEnemyFleetState(
        player: Player,
        @PathVariable gameId: Int,
    ) = doApiTask {
        val input = Fleet(gameId, false)
        ResponseEntity
            .status(Successes.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(services.inGameServices.fleetState(player, input)) {
                    link(Uris.enemyFleetState(gameId), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                }
            )
    }
}