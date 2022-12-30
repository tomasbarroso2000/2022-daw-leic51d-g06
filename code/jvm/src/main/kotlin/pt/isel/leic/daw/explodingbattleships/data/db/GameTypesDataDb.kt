package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.GameTypesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.GameType
import pt.isel.leic.daw.explodingbattleships.domain.GameTypeWithFleet
import pt.isel.leic.daw.explodingbattleships.domain.ShipType

class GameTypesDataDb : GameTypesData {

    override fun getGameType(transaction: Transaction, gameTypeName: String): GameTypeWithFleet? =
        (transaction as TransactionDataDb).withHandle { handle ->
            val gameType = handle.createQuery("select * from game_types where name = :gameType")
                .bind("gameType", gameTypeName)
                .mapTo<GameType>().firstOrNull() ?: return@withHandle null
            val fleet = handle.createQuery("select * from ship_types where game_type = :gameType")
                .bind("gameType", gameTypeName)
                .mapTo<ShipType>().list()
            GameTypeWithFleet(
                gameType.name,
                gameType.boardSize,
                gameType.shotsPerRound,
                gameType.layoutDefTimeInSecs,
                gameType.shootingTimeInSecs,
                fleet
            )
        }

    override fun getGameTypes(transaction: Transaction): List<GameTypeWithFleet> =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from game_types")
                .mapTo<GameType>().list().map { gameType ->
                    val fleet = handle.createQuery("select * from ship_types where game_type = :gameType")
                        .bind("gameType", gameType.name)
                        .mapTo<ShipType>().list()
                    GameTypeWithFleet(
                        gameType.name,
                        gameType.boardSize,
                        gameType.shotsPerRound,
                        gameType.layoutDefTimeInSecs,
                        gameType.shootingTimeInSecs,
                        fleet
                    )
                }
        }

    override fun createGameType(
        transaction: Transaction,
        gameType: String,
        boardSize: Int,
        shotsPerRound: Int,
        layoutDefTime: Int,
        shootingTime: Int,
        fleet: List<ShipType>
    ) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "insert into game_types " +
                    "values (:gameType, :boardSize, :shotsPerRound, :layoutDefTime, :shootingTime)"
            )
                .bind("gameType", gameType)
                .bind("boardSize", boardSize)
                .bind("shotsPerRound", shotsPerRound)
                .bind("layoutDefTime", layoutDefTime)
                .bind("shootingTime", shootingTime)
                .execute()
        }
    }
}