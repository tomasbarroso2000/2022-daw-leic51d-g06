package pt.isel.leic.daw.explodingbattleships.services

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.domain.Home
import pt.isel.leic.daw.explodingbattleships.services.comp.GameServices
import pt.isel.leic.daw.explodingbattleships.services.comp.PlayerServices
import pt.isel.leic.daw.explodingbattleships.services.comp.RankingServices

/**
 * Represents the services module of the app
 * @property gameServices the games section
 * @property playerServices the players section
 * @property rankingServices the ranking section
 */
class Services(data: Data) {
    val gameServices = GameServices(data)
    val playerServices = PlayerServices(data)
    val rankingServices = RankingServices(data)

    /**
     * Get home information
     */
    fun getHome() = Home()
}
