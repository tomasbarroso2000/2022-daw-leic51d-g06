package pt.isel.leic.daw.explodingbattleships.http.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.leic.daw.explodingbattleships.domain.Hits
import pt.isel.leic.daw.explodingbattleships.domain.Layout
import pt.isel.leic.daw.explodingbattleships.domain.User
import pt.isel.leic.daw.explodingbattleships.http.APPLICATION_SIREN
import pt.isel.leic.daw.explodingbattleships.http.Rels
import pt.isel.leic.daw.explodingbattleships.http.Successes
import pt.isel.leic.daw.explodingbattleships.http.Uris
import pt.isel.leic.daw.explodingbattleships.http.doApiTask
import pt.isel.leic.daw.explodingbattleships.http.models.FleetStateOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.GameOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.GameStateOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.HitsOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.LayoutOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.NumberOfPlayedGamesOutputModel
import pt.isel.leic.daw.explodingbattleships.infra.siren
import pt.isel.leic.daw.explodingbattleships.services.GamesServices
import javax.validation.Valid

@RestController
@RequestMapping(Uris.BASE_PATH)
class GamesController(private val services: GamesServices) {

    @GetMapping(Uris.Games.GAME_INFO)
    fun getGameInfo(
        user: User,
        @PathVariable gameId: Int
    ) = doApiTask {
        val res = services.getGame(user.id, gameId)
        ResponseEntity
            .status(HttpStatus.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(
                    TODO()
                    /*
                    GameOutputModel(
                        res.game.id,
                        res.game.type,
                        res.game.state,
                        1, // opponent
                        true, // playing
                        res.game.startedAt,
                        res.playerFleet,
                        //res.takenHits.map {it.square },
                        res.enemyFleet.filter { it.destroyed },
                        //res.sentHits.filter { it.onShip }.map { it.square },
                        //res.sentHits.filter { !it.onShip }.map { it.square }
                    )
                     */
                ) {
                    link(Uris.Games.gameInfo(gameId), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("GameOutputModel")
                }
            )
    }

    @GetMapping(Uris.Games.NR_OF_GAMES)
    fun getNrOfPlayedGames() =
        doApiTask {
            val res = services.getNumberOfPlayedGames()
            ResponseEntity
                .status(Successes.OK)
                .contentType(APPLICATION_SIREN)
                .body(
                    siren(NumberOfPlayedGamesOutputModel(res)) {
                        link(Uris.Games.nrOfGames(), Rels.SELF)
                        link(Uris.home(), Rels.HOME)
                        clazz("NumberOfPlayedGames")
                    }
                )
        }

    @GetMapping(Uris.Games.STATE)
    fun getGameState(@PathVariable gameId: Int) =
        doApiTask {
            val res = services.getGameState(gameId)
            ResponseEntity
            .status(Successes.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(GameStateOutputModel(res)) {
                    link(Uris.Games.state(gameId), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("GameState")
                }
            )
        }

    @GetMapping(Uris.Games.PLAYER_FLEET)
    fun getPlayerFleetState(
        user: User,
        @PathVariable gameId: Int,
    ) = doApiTask {
        val res = services.fleetState(user.id, gameId, true)
        ResponseEntity
            .status(Successes.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(FleetStateOutputModel(res)) {
                    link(Uris.Games.playerFleet(gameId), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("FleetStateOutputModel")
                }
            )
    }

    @GetMapping(Uris.Games.ENEMY_FLEET)
    fun getEnemyFleetState(
        user: User,
        @PathVariable gameId: Int,
    ) = doApiTask {
        val res = services.fleetState(user.id, gameId, false)
        ResponseEntity
            .status(Successes.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(FleetStateOutputModel(res)) {
                    link(Uris.Games.enemyFleet(gameId), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("FleetStateOutputModel")
                }
            )
    }

    @PutMapping(Uris.Games.SEND_HITS)
    fun sendHits(
        user: User,
        @Valid @RequestBody input: Hits,
    ) = doApiTask {
        val res = services.sendHits(user.id, input.gameId, input.squares)
        ResponseEntity
            .status(Successes.CREATED)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(HitsOutputModel(res.hitsOutcome, res.win)) {
                    link(Uris.Games.sendHits(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("HitsOutcome")
                }
            )
    }

    @PutMapping(Uris.Games.DEFINE_LAYOUT)
    fun defineLayout(
        user: User,
        @Valid @RequestBody input: Layout,
    ) = doApiTask {
        val res = services.sendLayout(user.id, input.gameId, input.ships)
        ResponseEntity
            .status(Successes.CREATED)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(LayoutOutputModel(res)) {
                    link(Uris.Games.defineLayout(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("LayoutOutput")
                }
            )
    }
}