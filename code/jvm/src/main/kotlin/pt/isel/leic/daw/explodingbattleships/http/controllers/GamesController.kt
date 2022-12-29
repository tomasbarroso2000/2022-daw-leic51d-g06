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
import pt.isel.leic.daw.explodingbattleships.http.models.input.ForfeitInputModel
import pt.isel.leic.daw.explodingbattleships.http.models.input.HitsInputModel
import pt.isel.leic.daw.explodingbattleships.http.models.input.LayoutInputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.FleetStateOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.GameOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.GameStateOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.GameTypesOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.GamesOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.NumberOfPlayedGamesOutputModel
import pt.isel.leic.daw.explodingbattleships.http.toGameOutputModel
import pt.isel.leic.daw.explodingbattleships.infra.SirenBuilderScope
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
    ) = doApiTask {
        val res = services.getCurrentlyPlayingGames(user.id, limit, skip)
        ResponseEntity
            .status(HttpStatus.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(
                    GamesOutputModel(res.list.map { it.toGameOutputModel() }, res.hasMore)
                ) {
                    link(Uris.Games.games(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    for (game in res.list) {
                        link(Uris.Games.gameInfo(game.game.id), Rels.GAME)
                    }
                    link(Uris.Games.gameTypes(), Rels.GAME_TYPES)
                    clazz("GamesOutputModel")
                }
            )
    }

    /**
     * Handles a get request for the available game types
     */
    @GetMapping(Uris.Games.GAME_TYPES)
    fun getGameTypes() = doApiTask {
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
                    clazz("GameTypesOutputModel")
                }
            )
    }

    private fun attachGameEntities(sirenBuilderScope: SirenBuilderScope<GameOutputModel>, userId: Int, gameId: Int) {
        sirenBuilderScope.entity(value = GameStateOutputModel(services.getGameState(gameId)), rel = Rels.GAME_STATE) {
            link(Uris.Games.gameState(gameId), Rels.SELF)
        }
        sirenBuilderScope.entity(
            value = FleetStateOutputModel(services.fleetState(userId, gameId, true)),
            rel = Rels.PLAYER_FLEET
        ) {
            link(Uris.Games.playerFleet(gameId), Rels.SELF)
        }
        sirenBuilderScope.entity(
            value = FleetStateOutputModel(services.fleetState(userId, gameId, false)),
            rel = Rels.ENEMY_FLEET
        ) {
            link(Uris.Games.enemyFleet(gameId), Rels.SELF)
        }
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
                siren(res.toGameOutputModel()) {
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
                        action(
                            "forfeit",
                            Uris.Games.forfeit(),
                            HttpMethod.PUT,
                            MediaType.APPLICATION_JSON.toString()
                        ) {
                            hiddenField("gameId", res.game.id.toString())
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
                    link(Uris.Users.home(), Rels.USER_HOME)
                    link(Uris.home(), Rels.HOME)
                    attachGameEntities(this, user.id, gameId)
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
    @GetMapping(Uris.Games.GAME_STATE)
    fun getGameState(@PathVariable gameId: Int) =
        doApiTask {
            val res = services.getGameState(gameId)
            ResponseEntity
                .status(Successes.OK)
                .contentType(APPLICATION_SIREN)
                .body(
                    siren(GameStateOutputModel(res)) {
                        link(Uris.Games.gameState(gameId), Rels.SELF)
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
                siren(res.toGameOutputModel()) {
                    link(Uris.Games.sendHits(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    link(Uris.Games.gameInfo(input.gameId), Rels.GAME)
                    attachGameEntities(this, user.id, input.gameId)
                    clazz("GameOutputModel")
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
                siren(res.toGameOutputModel()) {
                    link(Uris.Games.defineLayout(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    link(Uris.Games.gameInfo(input.gameId), Rels.GAME)
                    attachGameEntities(this, user.id, input.gameId)
                    clazz("GameOutputModel")
                }
            )
    }

    /**
     * Handles the put request for forfeiting
     * @param user the user that sent the request
     * @param input the input with the game id
     */
    @PutMapping(Uris.Games.FORFEIT)
    fun forfeit(
        user: User,
        @RequestBody
        input: ForfeitInputModel
    ) = doApiTask {
        val res = services.forfeit(user.id, input.gameId)
        ResponseEntity
            .status(Successes.CREATED)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(res.toGameOutputModel()) {
                    link(Uris.Games.forfeit(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    attachGameEntities(this, user.id, input.gameId)
                    clazz("GameOutputModel")
                }
            )
    }
}