package pt.isel.leic.daw.explodingbattleships.domain

import java.time.Instant

data class Token(
    val tokenVer: String,
    val userId: Int,
    val createdAt: Instant,
    val lastUsedAt: Instant
)