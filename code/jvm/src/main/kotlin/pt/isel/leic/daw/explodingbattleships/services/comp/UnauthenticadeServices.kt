package pt.isel.leic.daw.explodingbattleships.services.comp

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.isPasswordInvalid
import pt.isel.leic.daw.explodingbattleships.domain.SystemInfo
import pt.isel.leic.daw.explodingbattleships.domain.ListOfData
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.domain.PlayerInput
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppExceptionStatus
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.checkLimitAndSkip
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.doService

class UnauthenticatedServices(private val data: Data) {
    fun createPlayer(playerInput: PlayerInput) = doService(data) {
        if (playerInput.name.isNullOrBlank())
            throw AppException("Invalid name", AppExceptionStatus.BAD_REQUEST)
        if (playerInput.email.isNullOrBlank())
            throw AppException("Invalid email", AppExceptionStatus.BAD_REQUEST)
        if (playerInput.password == null || playerInput.password.isPasswordInvalid())
            throw AppException("Invalid password", AppExceptionStatus.BAD_REQUEST)
        data.playersData.createPlayer(it, playerInput.name, playerInput.email, playerInput.password.hashCode())
            ?: throw AppException("Could not create player", AppExceptionStatus.INTERNAL)
    }

    fun getNumberOfPlayedGames() = doService(data) { transaction ->
        data.gamesData.getNumberOfPlayedGames(transaction)
    }

    fun getGameState(gameId: Int?) = doService(data) { transaction ->
        if (gameId == null || gameId <= 0)
            throw AppException("Invalid gameId", AppExceptionStatus.BAD_REQUEST)
        data.gamesData.getGameState(transaction, gameId)
            ?: throw AppException("Game does not exist", AppExceptionStatus.NOT_FOUND)
    }

    fun getRankings(limit: Int, skip: Int): ListOfData<Player> = doService(data) { transaction ->
        checkLimitAndSkip(limit, skip)
        data.playersData.getRankings(transaction, limit, skip)
    }

    /**
     * Get home information
     */
    fun getSystemInfo() = SystemInfo()
}