package pt.isel.leic.daw.explodingbattleships.http.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyInput
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.http.OK
import pt.isel.leic.daw.explodingbattleships.http.Uris.BASE_PATH
import pt.isel.leic.daw.explodingbattleships.http.token
import pt.isel.leic.daw.explodingbattleships.http.doApiTask
import pt.isel.leic.daw.explodingbattleships.services.Services
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

const val PLAYER_INFO = "player/info"
const val ENTER_LOBBY = "lobby/enter"

@RestController
@RequestMapping(BASE_PATH)
class AuthenticatedApi(private val services: Services) {

    @GetMapping(PLAYER_INFO)
    fun handlerPlayerInfo(
        player: Player
    ) = doApiTask {
        ResponseEntity
            .status(OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(player)
    }

    @PostMapping(ENTER_LOBBY)
    fun handlerEnterLobby(
        player: Player,
        @Valid @RequestBody input: EnterLobbyInput
    ) = doApiTask {
        ResponseEntity
            .status(OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.authenticatedServices.enterLobby(player, input))
    }
}