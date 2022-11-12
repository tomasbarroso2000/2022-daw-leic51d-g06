package pt.isel.leic.daw.explodingbattleships.data.db

import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.daw.explodingbattleships.data.GameTypesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.GameType

class GameTypesDataDb : GameTypesData {
    override fun getGameType(transaction: Transaction, gameType: String): GameType? =
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createQuery("select * from game_types where name = :gameType")
                .bind("gameType", gameType)
                .mapTo<GameType>().firstOrNull()
        }

    override fun createGameType(
        transaction: Transaction,
        gameType: String,
        boardSize: Int,
        shotsPerRound: Int,
        layoutDefTime: Int,
        shootingTime: Int
    ) {
        (transaction as TransactionDataDb).withHandle { handle ->
            handle.createUpdate(
                "insert into game_types " +
                    "(name, board_size, shots_per_round , layout_def_time_in_secs , shooting_time_in_secs ) " +
                    "values (:gameType, :boardSize, :shotsPerRound, :layoutDefTime, :shootingTime)"
            )
                .bind("gameType", gameType)
                .bind("boardSize", boardSize)
                .bind("shotsPerRound", shotsPerRound)
                .bind("layoutDefTime", layoutDefTime)
                .bind("shootingTime", shootingTime)
                .executeAndReturnGeneratedKeys()
                .mapTo<Int>()
                .first()
        }
    }
}