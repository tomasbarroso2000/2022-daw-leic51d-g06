package pt.isel.leic.daw.explodingbattleships.utils

import pt.isel.leic.daw.explodingbattleships.domain.TokenValidationInfo

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
    fun validate(validationInfo: TokenValidationInfo, token: String): Boolean
}