package pt.isel.leic.daw.explodingbattleships.services

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.services.comp.AuthenticatedServices
import pt.isel.leic.daw.explodingbattleships.services.comp.InGameServices
import pt.isel.leic.daw.explodingbattleships.services.comp.UnauthenticatedServices

/**
 * Represents the services module of the app
 * @property inGameServices the in-game section
 * @property authenticatedServices the authentication-protected section
 * @property unauthenticatedServices the unprotected section
 */
class Services(data: Data) {
    val inGameServices = InGameServices(data)
    val authenticatedServices = AuthenticatedServices(data)
    val unauthenticatedServices = UnauthenticatedServices(data)
}
