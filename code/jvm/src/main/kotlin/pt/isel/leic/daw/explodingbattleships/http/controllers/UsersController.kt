package pt.isel.leic.daw.explodingbattleships.http.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyInput
import pt.isel.leic.daw.explodingbattleships.domain.User
import pt.isel.leic.daw.explodingbattleships.domain.UserInput
import pt.isel.leic.daw.explodingbattleships.http.Rels
import pt.isel.leic.daw.explodingbattleships.http.Successes
import pt.isel.leic.daw.explodingbattleships.http.Uris
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.CREATE
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.ENTER_LOBBY
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.HOME
import pt.isel.leic.daw.explodingbattleships.http.Uris.Users.RANKINGS
import pt.isel.leic.daw.explodingbattleships.http.doApiTask
import pt.isel.leic.daw.explodingbattleships.infra.siren
import pt.isel.leic.daw.explodingbattleships.services.Services
import javax.validation.Valid

@RestController
@RequestMapping(Uris.BASE_PATH)
class UsersController(private val services: Services) {

    @PostMapping(CREATE)
    fun createUser(
        @Valid @RequestBody input: UserInput
    ) = doApiTask {
        ResponseEntity
            .status(Successes.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(services.usersServices.createUser(input)) {
                    link(Uris.Users.createPlayer(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("UserOutput")
                }
            )
    }

    @GetMapping(HOME)
    fun getPlayerHome(
        player: User
    ) = doApiTask {
        ResponseEntity
            .status(Successes.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(player) {
                    link(Uris.Users.home(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("PlayerOutputModel")
                }
            )
    }

    @GetMapping(RANKINGS)
    fun getRankings(
        @RequestParam(required = false, defaultValue = "10") limit: Int,
        @RequestParam(required = false, defaultValue = "0") skip: Int,
    ) = doApiTask {
        ResponseEntity
            .status(Successes.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(services.usersServices.getRankings(limit, skip)) {
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
        ResponseEntity
            .status(Successes.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(services.usersServices.enterLobby(player, input)) {
                    link(Uris.Users.enterLobby(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("EnterLobbyOutput")
                }
            )
    }
}