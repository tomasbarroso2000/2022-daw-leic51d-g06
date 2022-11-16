package pt.isel.leic.daw.explodingbattleships.http.controllers

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.daw.explodingbattleships.domain.User
import pt.isel.leic.daw.explodingbattleships.http.APPLICATION_SIREN
import pt.isel.leic.daw.explodingbattleships.http.Rels
import pt.isel.leic.daw.explodingbattleships.http.Successes
import pt.isel.leic.daw.explodingbattleships.http.Uris
import pt.isel.leic.daw.explodingbattleships.http.doApiTask
import pt.isel.leic.daw.explodingbattleships.http.models.input.HitsInputModel
import pt.isel.leic.daw.explodingbattleships.http.models.input.LayoutInputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.FleetStateOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.GameOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.GameStateOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.GameTypesOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.GamesOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.HitsOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.LayoutOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.NumberOfPlayedGamesOutputModel
import pt.isel.leic.daw.explodingbattleships.infra.siren
import pt.isel.leic.daw.explodingbattleships.services.GamesServices
import javax.validation.Valid

/**
 * The games controller
 */
@RestController
@RequestMapping(Uris.BASE_PATH)
class GamesController(private val services: GamesServices) {

    /**
     * Handles a get request for the list of games the player is currently playing
     * @param user the user that sent the request
     */
    @GetMapping(Uris.Games.GAMES)
    fun getCurrentlyPlayingGames(
        @RequestParam(required = false, defaultValue = "10") limit: Int,
        @RequestParam(required = false, defaultValue = "0") skip: Int,
        user: User
    ) =
        doApiTask {
            val res = services.getCurrentlyPlayingGames(user.id, limit, skip)
            ResponseEntity
                .status(HttpStatus.OK)
                .contentType(APPLICATION_SIREN)
                .body(
                    siren(
                        GamesOutputModel(res.list, res.hasMore)
                    ) {
                        link(Uris.Games.games(), Rels.SELF)
                        link(Uris.home(), Rels.HOME)
                        for (game in res.list) {
                            link(Uris.Games.gameInfo(game.id), Rels.GAME)
                        }
                        clazz("GamesOutputModel")
                    }
                )
        }

    /**
     * Handles a get request for the available game types
     */
    @GetMapping(Uris.Games.GAME_TYPES)
    fun getGameTypes() =
        doApiTask {
            val res = services.getGameTypesAndShips()
            ResponseEntity
                .status(HttpStatus.OK)
                .contentType(APPLICATION_SIREN)
                .body(
                    siren(
                        GameTypesOutputModel(res)
                    ) {
                        link(Uris.Games.gameTypes(), Rels.SELF)
                        link(Uris.home(), Rels.HOME)
                    }
                )
        }

    /**
     * Handles a get request for the game info
     * @param user the user that sent the request
     * @param gameId the game id
     */
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
                    GameOutputModel(
                        res.game.id,
                        res.game.type,
                        res.game.state,
                        res.opponent,
                        res.playing,
                        res.game.startedAt,
                        res.playerFleet,
                        res.takenHits,
                        res.enemySunkFleet,
                        res.hits,
                        res.misses
                    )
                ) {
                    link(Uris.Games.gameInfo(gameId), Rels.SELF)
                    if (res.game.state == "shooting") {
                        action(
                            "send-hits",
                            Uris.Games.sendHits(),
                            HttpMethod.PUT,
                            MediaType.APPLICATION_JSON.toString()
                        ) {
                            hiddenField("gameId", res.game.id.toString())
                            textField("squares")
                        }
                    }
                    if (res.game.state == "layout_definition") {
                        action(
                            "define-layout",
                            Uris.Games.defineLayout(),
                            HttpMethod.PUT,
                            MediaType.APPLICATION_JSON.toString()
                        ) {
                            hiddenField("gameId", res.game.id.toString())
                            textField("ships")
                        }
                    }
                    link(Uris.Users.home(), Rels.USER)
                    link(Uris.home(), Rels.HOME)
                    clazz("GameOutputModel")
                }
            )
    }

    /**
     * Handles a get request for the total number of played games
     */
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
                        clazz("NumberOfPlayedGamesOutputModel")
                    }
                )
        }

    /**
     * Handles a get request for the game state
     * @param gameId the game id
     */
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
                        link(Uris.Games.gameInfo(gameId), Rels.GAME)
                        clazz("GameStateOutputModel")
                    }
                )
        }

    /**
     * Handles a get request for the player fleet state
     * @param user the user that sent the request
     * @param gameId the game id
     */
    @GetMapping(Uris.Games.PLAYER_FLEET)
    fun getPlayerFleetState(
        user: User,
        @PathVariable gameId: Int
    ) = doApiTask {
        val res = services.fleetState(user.id, gameId, true)
        ResponseEntity
            .status(Successes.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(FleetStateOutputModel(res)) {
                    link(Uris.Games.playerFleet(gameId), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    link(Uris.Games.gameInfo(gameId), Rels.GAME)
                    clazz("FleetStateOutputModel")
                }
            )
    }

    /**
     * Handles a get request for the enemy fleet state
     * @param user the user that sent the request
     * @param gameId the game id
     */
    @GetMapping(Uris.Games.ENEMY_FLEET)
    fun getEnemyFleetState(
        user: User,
        @PathVariable gameId: Int
    ) = doApiTask {
        val res = services.fleetState(user.id, gameId, false)
        ResponseEntity
            .status(Successes.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(FleetStateOutputModel(res)) {
                    link(Uris.Games.enemyFleet(gameId), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    link(Uris.Games.gameInfo(gameId), Rels.GAME)
                    clazz("FleetStateOutputModel")
                }
            )
    }

    /**
     * Handles the put request for sending hits
     * @param user the user that sent the request
     * @param input the hit's input model that represents the list of squares hit and the game id
     */
    @PutMapping(Uris.Games.SEND_HITS)
    fun sendHits(
        user: User,
        @Valid
        @RequestBody
        input: HitsInputModel
    ) = doApiTask {
        val res = services.sendHits(user.id, input.gameId, input.squares)
        ResponseEntity
            .status(Successes.CREATED)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(HitsOutputModel(res.hitsOutcome, res.win)) {
                    link(Uris.Games.sendHits(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    link(Uris.Games.gameInfo(input.gameId), Rels.GAME)
                    clazz("HitsOutputModel")
                }
            )
    }

    /**
     * Handles the put request for defining a layout
     * @param user the user that sent the request
     * @param input the layout input model that represents the list of ships and the game id
     */
    @PutMapping(Uris.Games.DEFINE_LAYOUT)
    fun defineLayout(
        user: User,
        @Valid
        @RequestBody
        input: LayoutInputModel
    ) = doApiTask {
        val res = services.sendLayout(user.id, input.gameId, input.ships)
        ResponseEntity
            .status(Successes.CREATED)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(LayoutOutputModel(res)) {
                    link(Uris.Games.defineLayout(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    link(Uris.Games.gameInfo(input.gameId), Rels.GAME)
                    clazz("LayoutOutputModel")
                }
            )
    }
}