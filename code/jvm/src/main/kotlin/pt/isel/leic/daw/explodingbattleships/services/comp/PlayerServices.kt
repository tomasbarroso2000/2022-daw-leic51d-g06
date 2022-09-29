package pt.isel.leic.daw.explodingbattleships.services.comp

import pt.isel.leic.daw.explodingbattleships.data.Data
import pt.isel.leic.daw.explodingbattleships.data.DataDb
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.isPasswordInvalid
import pt.isel.leic.daw.explodingbattleships.domain.PlayerInput
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppExceptionStatus
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.doService

class PlayerServices(private val data: Data) {
    fun createPlayer(playerInput: PlayerInput) = doService(data) {
        if (playerInput.name.isNullOrBlank())
            throw AppException("Invalid name", AppExceptionStatus.BAD_REQUEST)
        if (playerInput.email.isNullOrBlank())
            throw AppException("Invalid email", AppExceptionStatus.BAD_REQUEST)
        if (playerInput.password == null || playerInput.password.isPasswordInvalid())
            throw AppException("Invalid password", AppExceptionStatus.BAD_REQUEST)
        data.playerData.createPlayer(it, playerInput.name, playerInput.email, playerInput.password.hashCode())
            ?: throw AppException("Could not create player", AppExceptionStatus.INTERNAL)
    }
}

