package pt.isel.leic.daw.explodingbattleships.http.pipeline

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pt.isel.leic.daw.explodingbattleships.domain.User
import pt.isel.leic.daw.explodingbattleships.http.getTokenFromAuthorization
import pt.isel.leic.daw.explodingbattleships.services.UsersServices

@Component
class AuthorizationHeaderProcessor(
    val services: UsersServices
) {

    fun process(authorizationValue: String?): User {
        val token = getTokenFromAuthorization(authorizationValue)
        return services.getPlayerInfo(token)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthorizationHeaderProcessor::class.java)
        const val SCHEME = "bearer"
    }
}
