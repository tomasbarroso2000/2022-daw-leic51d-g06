package pt.isel.leic.daw.explodingbattleships.services


import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import pt.isel.leic.daw.explodingbattleships.data.Data

/**
 * Represents the services module of the app
 * @property inGameServices the in-game section
 * @property authenticatedServices the authentication-protected section
 * @property unauthenticatedServices the unprotected section
 */
@Component
class Services(val data: Data) {
    val inGameServices = InGameServices(data)
    val authenticatedServices = AuthenticatedServices(data)
    val unauthenticatedServices = UnauthenticatedServices(data)
}
