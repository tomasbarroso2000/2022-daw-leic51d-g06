package pt.isel.leic.daw.explodingbattleships.http.controllers

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.daw.explodingbattleships.http.APPLICATION_SIREN
import pt.isel.leic.daw.explodingbattleships.http.Rels
import pt.isel.leic.daw.explodingbattleships.http.Successes
import pt.isel.leic.daw.explodingbattleships.http.Uris
import pt.isel.leic.daw.explodingbattleships.http.Uris.BASE_PATH
import pt.isel.leic.daw.explodingbattleships.http.doApiTask
import pt.isel.leic.daw.explodingbattleships.http.models.output.HomeOutputModel
import pt.isel.leic.daw.explodingbattleships.infra.siren

/**
 * The Home controller
 */
@RestController
@RequestMapping(BASE_PATH)
class HomeController {

    /**
     * Handles a get request for the home resource
     */
    @GetMapping
    fun getHome() = doApiTask {
        ResponseEntity
            .status(Successes.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(HomeOutputModel()) {
                    action(
                        "create-user",
                        Uris.Users.createUser(),
                        HttpMethod.POST,
                        MediaType.APPLICATION_JSON.toString()
                    ) {
                        textField("name")
                        textField("email")
                        textField("password")
                    }
                    action(
                        "create-token",
                        Uris.Users.createToken(),
                        HttpMethod.POST,
                        MediaType.APPLICATION_JSON.toString()
                    ) {
                        textField("email")
                        textField("password")
                    }
                    link(Uris.home(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    link(Uris.Users.rankings(), Rels.RANKINGS)
                    link(Uris.Games.nrOfGames(), Rels.NR_OF_TOTAL_GAMES)
                    clazz("HomeOutputModel")
                }
            )
    }
}