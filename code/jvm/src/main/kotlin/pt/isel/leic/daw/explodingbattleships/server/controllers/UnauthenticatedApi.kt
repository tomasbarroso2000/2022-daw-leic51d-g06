package pt.isel.leic.daw.explodingbattleships.server.controllers
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.daw.explodingbattleships.server.ClientIp
import pt.isel.leic.daw.explodingbattleships.server.OK
import pt.isel.leic.daw.explodingbattleships.server.doApiTask
import pt.isel.leic.daw.explodingbattleships.services.Services
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.Size

/*
 * Parameter binding examples
 */

data class StudentInputModel(
    @get:Size(min = 1, max = 256)
    val name: String,

    @get:Min(1)
    val number: Int,

    @get:Min(1970)
    val enrollmentYear: Int
)

const val BASE_PATH = "/battleships/"
const val RANKINGS = "rankings"
const val NUMBER_OF_PLAYED_GAMES = "games/total"
const val GAME_STATE = "games/state/{id}"

@RestController
@RequestMapping(BASE_PATH)
class UnauthenticatedApi(private val services: Services) {

    @GetMapping(RANKINGS)
    fun handlerRankings(
        @RequestParam limit: Int,
        @RequestParam skip: Int,
    ) = doApiTask {
        ResponseEntity
            .status(OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.unauthenticatedServices.getRankings(limit, skip))
    }

    @GetMapping(NUMBER_OF_PLAYED_GAMES)
    fun handlerNrOfPlayedGames() =
        doApiTask {
            ResponseEntity
                .status(OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(services.unauthenticatedServices.getNumberOfPlayedGames())
        }


    // Binding query string values to arguments
    @GetMapping(GAME_STATE)
    fun handlerGameState(@RequestParam id: Int) =
        doApiTask { ResponseEntity
            .status(OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(services.unauthenticatedServices.getGameState(id)) }



    // Binding all query string pairs to an arguments
    @GetMapping("3")
    fun handler3(
        @RequestParam prms: MultiValueMap<String, String>,
    ) = prms
        .map { "${it.key}: ${it.value.joinToString(", ", "[", "]")}\n" }
        .joinToString()

    // Using a custom ArgumentResolver to bind a custom type to an arguments
    @GetMapping("4")
    fun handler4(
        clientIp: ClientIp,
    ) = "Hello ${clientIp.ipAddress}"

    // Using generic argument resolution, supporting JSON
    @PostMapping("5")
    fun handler5(
        @Valid @RequestBody input: StudentInputModel,
    ) = "Received student with name=${input.name}, number=${input.number}, and enrollment year=${input.enrollmentYear}"

    // Binding the raw HttpServletRequest to the request
    @PostMapping("6")
    fun handler6(
        request: HttpServletRequest,
        @Valid @RequestBody input: StudentInputModel,
    ) = "Accept: ${request.getHeader("Accept")}, Body: $input }"

    @GetMapping("7/{aid}/b/{bid}")
    fun handler7(
        @PathVariable aid: Int,
        @PathVariable bid: String,
        req: HttpServletRequest
    ) = "handler7 with aid=$aid and bid=$bid"


}
