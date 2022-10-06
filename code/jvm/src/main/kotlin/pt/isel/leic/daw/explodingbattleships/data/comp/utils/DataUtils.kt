package pt.isel.leic.daw.explodingbattleships.data.comp.utils

fun String.isPasswordInvalid(): Boolean =
    !any { it.isDigit() } || !any { it.isUpperCase() }