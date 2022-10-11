package pt.isel.leic.daw.explodingbattleships.server.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.leic.daw.explodingbattleships.domain.Hits
import pt.isel.leic.daw.explodingbattleships.domain.Layout
import pt.isel.leic.daw.explodingbattleships.server.CREATED
import pt.isel.leic.daw.explodingbattleships.server.doApiTask
import pt.isel.leic.daw.explodingbattleships.server.token
import pt.isel.leic.daw.explodingbattleships.services.Services
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

const val SEND_HITS = "games/hit"
const val DEFINE_LAYOUT = "games/layout"

@RestController
@RequestMapping(BASE_PATH)
class InGameApi(private val services: Services) {
    @PostMapping(DEFINE_LAYOUT)
    fun defineLayout(
        request: HttpServletRequest,
        @Valid @RequestBody input: Layout,
    ) = doApiTask {
        val token = request.token
        ResponseEntity
            .status(CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.inGameServices.defineLayout(token, input))
    }

    @PostMapping(SEND_HITS)
    fun sendHits(
        request: HttpServletRequest,
        @Valid @RequestBody input: Hits,
    ) = doApiTask {
        val token = request.token
        ResponseEntity
            .status(CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.inGameServices.sendHits(token, input))
    }
}