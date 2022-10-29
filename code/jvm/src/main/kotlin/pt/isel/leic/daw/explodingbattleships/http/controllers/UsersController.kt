package pt.isel.leic.daw.explodingbattleships.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.CREATE
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.ENTERED_GAME
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.ENTER_LOBBY
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.HOME
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.RANKINGS
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.TOKEN
import pt.isel.leic.daw.explodingbattleships.http.doApiTask
import pt.isel.leic.daw.explodingbattleships.http.models.input.LobbyInputModel
import pt.isel.leic.daw.explodingbattleships.http.models.input.UserInputModel
import pt.isel.leic.daw.explodingbattleships.http.models.input.UserTokenInputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.EnteredGameOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.LobbyOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.RankingsOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.UserCreationOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.UserOutputModel
import pt.isel.leic.daw.explodingbattleships.http.models.output.UserTokenOutputModel
import pt.isel.leic.daw.explodingbattleships.infra.siren
import pt.isel.leic.daw.explodingbattleships.services.UsersServices
import javax.validation.Valid

/**
 * The Users controller
 */
@RestController
@RequestMapping(Uris.BASE_PATH)
class UsersController(private val services: UsersServices) {

    /**
     * Handles a get request for the player home resource
     * @param user the user that sent the request
     */
    @GetMapping(HOME)
    fun getPlayerHome(
        user: User
    ) = doApiTask {
        ResponseEntity
            .status(Successes.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(UserOutputModel(user.id, user.name, user.email, user.score)) {
                    link(Uris.Users.home(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("UserOutputModel")
                }
            )
    }

    /**
     * Handles a post request for creating a user
     * @param input the user input model that represents the user's name, email and password
     */
    @PostMapping(CREATE)
    fun createUser(
        @Valid
        @RequestBody
        input: UserInputModel
    ) = doApiTask {
        val res = services.createUser(input.name, input.email, input.password)
        ResponseEntity
            .status(Successes.CREATED)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(UserCreationOutputModel(res)) {
                    link(Uris.Users.createUser(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("UserCreationOutputModel")
                }
            )
    }

    /**
     * Handles a post request for creating a token
     * @param input the user token input model that represents the user's email and password
     */
    @PostMapping(TOKEN)
    fun createToken(
        @Valid
        @RequestBody
        input: UserTokenInputModel
    ) = doApiTask {
        val res = services.createToken(input.email, input.password)
        ResponseEntity
            .status(Successes.CREATED)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(UserTokenOutputModel(res)) {
                    link(Uris.Users.createToken(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("UserTokenOutputModel")
                }
            )
    }

    /**
     * Handles a get request for the rankings
     * @param limit the last index of the rankings list desired
     * @param skip the first index of the rankings list desired
     */
    @GetMapping(RANKINGS)
    fun getRankings(
        @RequestParam(required = false, defaultValue = "10") limit: Int,
        @RequestParam(required = false, defaultValue = "0") skip: Int
    ) = doApiTask {
        val res = services.getRankings(limit, skip)
        ResponseEntity
            .status(Successes.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(RankingsOutputModel(res)) {
                    link(Uris.Users.rankings(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("RankingsOutputModel")
                }
            )
    }

    /**
     * Handles a post request for entering a lobby
     * @param user the user that sent the request
     * @param input the lobby input model that represents the game type the user desires to play
     */
    @PostMapping(ENTER_LOBBY)
    fun enterLobby(
        user: User,
        @Valid
        @RequestBody
        input: LobbyInputModel
    ) = doApiTask {
        val res = services.enterLobby(user.id, input.gameType)
        ResponseEntity
            .status(Successes.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(LobbyOutputModel(res.enteredLobby, res.lobbyOrGameId)) {
                    link(Uris.Users.enterLobby(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("LobbyOutputModel")
                }
            )
    }

    @PutMapping(ENTERED_GAME)
    fun enteredGame(
        user: User,
        @PathVariable lobbyId: Int
    ) = doApiTask {
        val res = services.enteredGame(user.id, lobbyId)
        ResponseEntity
            .status(Successes.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(EnteredGameOutputModel(res)) {
                    link(Uris.Users.enteredGame(lobbyId), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    if (res != null) {
                        link(Uris.Games.gameInfo(res), Rels.GAME)
                    }
                    clazz("EnteredGameOutputModel")
                }
            )
    }
}