package pt.isel.leic.daw.explodingbattleships.http.pipeline

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.services.Services
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException

@Component
class AuthorizationHeaderProcessor(
    val services: Services
) {

    fun process(authorizationValue: String?): Player? {
        if (authorizationValue == null)
            return null
        val parts = authorizationValue.trim().split(" ")
        if (parts.size != 2)
            return null
        if (parts[0].lowercase() != SCHEME)
            return null
        return try {
            services.authenticatedServices.getPlayerInfo(parts[1])
        } catch (e: AppException) {
            null
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthorizationHeaderProcessor::class.java)
        const val SCHEME = "bearer"
    }
}