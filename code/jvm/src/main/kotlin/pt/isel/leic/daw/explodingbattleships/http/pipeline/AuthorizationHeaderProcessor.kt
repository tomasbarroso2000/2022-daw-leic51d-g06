package pt.isel.leic.daw.explodingbattleships.http.pipeline

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.http.getTokenFromAuthorization
import pt.isel.leic.daw.explodingbattleships.services.Services
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException

@Component
class AuthorizationHeaderProcessor(
    val services: Services
) {

    fun process(authorizationValue: String?): Player {
        val token = getTokenFromAuthorization(authorizationValue)
        return services.authenticatedServices.getPlayerInfo(token)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthorizationHeaderProcessor::class.java)
        const val SCHEME = "bearer"
    }
}