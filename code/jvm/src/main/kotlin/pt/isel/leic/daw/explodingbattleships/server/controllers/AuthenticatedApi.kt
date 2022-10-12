package pt.isel.leic.daw.explodingbattleships.server.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.daw.explodingbattleships.domain.EnterLobbyInput
import pt.isel.leic.daw.explodingbattleships.server.OK
import pt.isel.leic.daw.explodingbattleships.server.token
import pt.isel.leic.daw.explodingbattleships.server.doApiTask
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
        request: HttpServletRequest
    ) = doApiTask {
        val token = request.token
        ResponseEntity
            .status(OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.authenticatedServices.getPlayerInfo(token))
    }

    @PostMapping(ENTER_LOBBY)
    fun handlerEnterLobby(
        request: HttpServletRequest,
        @Valid @RequestBody input: EnterLobbyInput
    ) = doApiTask {
        val token = request.token
        ResponseEntity
            .status(OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.authenticatedServices.enterLobby(token, input))
    }
}