package pt.isel.leic.daw.explodingbattleships.services


import org.springframework.stereotype.Component
import pt.isel.leic.daw.explodingbattleships.data.Data

/**
 * Represents the services module of the app
 * @property usersServices the users section
 * @property gamesServices the games section
 */
@Component
class Services(val data: Data) {
    val usersServices = UsersServices(data)
    val gamesServices = GamesServices(data)
}
