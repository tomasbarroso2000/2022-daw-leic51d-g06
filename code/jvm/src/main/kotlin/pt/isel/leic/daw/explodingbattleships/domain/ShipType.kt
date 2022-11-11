package pt.isel.leic.daw.explodingbattleships.domain

/**
 * Represents a ship type
 * @property name the ship name
 * @property size the ship size
 * @property gameType the game type
 */
data class ShipType(val name: String, val size: Int, val gameType: String) {
    init {
        require(name.isNotEmpty())
        require(size > 0)
        require(gameType.isNotEmpty())
    }
}