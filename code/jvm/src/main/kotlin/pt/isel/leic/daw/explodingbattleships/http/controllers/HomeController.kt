package pt.isel.leic.daw.explodingbattleships.http.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.daw.explodingbattleships.http.*
import pt.isel.leic.daw.explodingbattleships.http.Uris.BASE_PATH
import pt.isel.leic.daw.explodingbattleships.http.models.HomeOutputModel
import pt.isel.leic.daw.explodingbattleships.infra.siren

@RestController
@RequestMapping(BASE_PATH)
class HomeController {

    @GetMapping
    fun getHome() = doApiTask {
        ResponseEntity
            .status(Successes.OK)
            .contentType(APPLICATION_SIREN)
            .body(
                siren(HomeOutputModel()){
                    link(Uris.home(), Rels.SELF)
                    link(Uris.home(), Rels.HOME)
                    clazz("HomeOutputModel")
                }
            )
    }
}
