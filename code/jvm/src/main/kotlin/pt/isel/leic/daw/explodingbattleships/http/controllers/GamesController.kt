package pt.isel.leic.daw.explodingbattleships.http.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.leic.daw.explodingbattleships.domain.Fleet
import pt.isel.leic.daw.explodingbattleships.domain.Hits
import pt.isel.leic.daw.explodingbattleships.domain.Layout
import pt.isel.leic.daw.explodingbattleships.domain.User
import pt.isel.leic.daw.explodingbattleships.http.Rels
import pt.isel.leic.daw.explodingbattleships.http.Successes
import pt.isel.leic.daw.explodingbattleships.http.Uris
import pt.isel.leic.daw.explodingbattleships.http.doApiTask
import pt.isel.leic.daw.explodingbattleships.infra.siren
import pt.isel.leic.daw.explodingbattleships.services.Services
import javax.validation.Valid

@RestController
@RequestMapping(Uris.BASE_PATH)
class GamesController(private val services: Services) {

    @GetMapping(Uris.Games.NR_OF_GAMES)
    fun getNrOfPlayedGames() =
        doApiTask {
            ResponseEntity
                .status(Successes.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    siren(services.gamesServices.getNumberOfPlayedGames()) {
                        link(Uris.Games.nrOfGames(), Rels.SELF)
                        link(Uris.home(), Rels.HOME)
                        clazz("NumberOfPlayedGames")
                    }
                )
        }

    @GetMapping(Uris.Games.STATE)
    fun getGameState(@PathVariable gameId: Int) =
        doApiTask { ResponseEntity
            .status(Successes.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(services.gamesServices.getGameState(gameId)) {
                    link(Uris.Games.state(gameId), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("GameState")
                }
            )
        }

    @GetMapping(Uris.Games.PLAYER_FLEET)
    fun getPlayerFleetState(
        player: User,
        @PathVariable gameId: Int,
    ) = doApiTask {
        val input = Fleet(gameId, true)
        ResponseEntity
            .status(Successes.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(services.gamesServices.fleetState(player, input)) {
                    link(Uris.Games.playerFleet(gameId), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("List<ShipState>")
                }
            )
    }

    @GetMapping(Uris.Games.ENEMY_FLEET)
    fun getEnemyFleetState(
        player: User,
        @PathVariable gameId: Int,
    ) = doApiTask {
        val input = Fleet(gameId, false)
        ResponseEntity
            .status(Successes.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(services.gamesServices.fleetState(player, input)) {
                    link(Uris.Games.enemyFleet(gameId), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("List<ShipState>")
                }
            )
    }

    @PutMapping(Uris.Games.SEND_HITS)
    fun sendHits(
        player: User,
        @Valid @RequestBody input: Hits,
    ) = doApiTask {
        ResponseEntity
            .status(Successes.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(services.gamesServices.sendHits(player, input)) {
                    link(Uris.Games.sendHits(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("HitsOutcome")
                }
            )
    }

    @PutMapping(Uris.Games.DEFINE_LAYOUT)
    fun defineLayout(
        player: User,
        @Valid @RequestBody input: Layout,
    ) = doApiTask {
        ResponseEntity
            .status(Successes.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(services.gamesServices.sendLayout(player, input)) {
                    link(Uris.Games.defineLayout(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("LayoutOutput")
                }
            )
    }
}