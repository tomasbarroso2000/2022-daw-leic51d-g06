package pt.isel.leic.daw.explodingbattleships.http.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyInput
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyOutput
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.http.*
import pt.isel.leic.daw.explodingbattleships.http.Uris.BASE_PATH
import pt.isel.leic.daw.explodingbattleships.http.Uris.ENTER_LOBBY
import pt.isel.leic.daw.explodingbattleships.http.Uris.PLAYER_INFO
import pt.isel.leic.daw.explodingbattleships.infra.siren
import pt.isel.leic.daw.explodingbattleships.services.Services
import javax.validation.Valid

@RestController
@RequestMapping(BASE_PATH)
class AuthenticatedApi(private val services: Services) {

    @GetMapping(PLAYER_INFO)
    fun handlerPlayerInfo(
        player: Player
    ) = doApiTask {
        ResponseEntity
            .status(Successes.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(player) {
                    link(Uris.playerInfo(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("PlayerOutputModel")
                }
            )
    }

    @PostMapping(ENTER_LOBBY)
    fun handlerEnterLobby(
        player: Player,
        @Valid @RequestBody input: EnterLobbyInput
    ) = doApiTask {
        ResponseEntity
            .status(Successes.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                siren(services.authenticatedServices.enterLobby(player, input)) {
                    link(Uris.enterLobby(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("EnterLobbyOutput")
                }
            )
    }
}