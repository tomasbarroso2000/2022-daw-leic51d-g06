package pt.isel.leic.daw.explodingbattleships.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyInput
import pt.isel.leic.daw.explodingbattleships.domain.User
import pt.isel.leic.daw.explodingbattleships.domain.UserInput
import pt.isel.leic.daw.explodingbattleships.http.APPLICATION_SIREN
import pt.isel.leic.daw.explodingbattleships.http.Rels
import pt.isel.leic.daw.explodingbattleships.http.Successes
import pt.isel.leic.daw.explodingbattleships.http.Uris
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.CREATE
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.ENTER_LOBBY
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.HOME
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.RANKINGS
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.TOKEN
import pt.isel.leic.daw.explodingbattleships.http.doApiTask
import pt.isel.leic.daw.explodingbattleships.http.models.*
import pt.isel.leic.daw.explodingbattleships.infra.siren
import pt.isel.leic.daw.explodingbattleships.services.UsersServices
import javax.validation.Valid

@RestController
@RequestMapping(Uris.BASE_PATH)
class UsersController(private val services: UsersServices) {

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

    @PostMapping(CREATE)
    fun createUser(
        @Valid @RequestBody input: UserInput
    ) = doApiTask {
        val res = services.createUser(input.name, input.email, input.password)
        ResponseEntity
            .status(Successes.CREATED)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(UserCreationOutputModel(res.id)) {
                    link(Uris.Users.createUser(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("UserOutput")
                }
            )
    }

    @PostMapping(TOKEN)
    fun createToken(
        @Valid @RequestBody input: UserTokenInputModel
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

    @GetMapping(RANKINGS)
    fun getRankings(
        @RequestParam(required = false, defaultValue = "10") limit: Int,
        @RequestParam(required = false, defaultValue = "0") skip: Int,
    ) = doApiTask {
        val res = services.getRankings(limit, skip)
        ResponseEntity
            .status(Successes.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(RankingsOutputModel(res)) {
                    link(Uris.Users.rankings(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("Rankings")
                }
            )
    }

    @PostMapping(ENTER_LOBBY)
    fun enterLobby(
        player: User,
        @Valid @RequestBody input: EnterLobbyInput
    ) = doApiTask {
        val res = services.enterLobby(player.id, input.gameType)
        ResponseEntity
            .status(Successes.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(EnterLobbyOutputModel(res.waitingForGame, res.gameId)) {
                    link(Uris.Users.enterLobby(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("EnterLobbyOutput")
                }
            )
    }
}