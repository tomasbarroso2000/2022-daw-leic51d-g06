package pt.isel.leic.daw.explodingbattleships.services

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.leic.daw.explodingbattleships.data.DataMem
import pt.isel.leic.daw.explodingbattleships.services.comp.utils.AppException

class RankingServicesTests {
    private val data = DataMem()
    private val services = Services(data).rankingServices

    @Test
    fun get_rankings() {
        val rankings = services.getRankings(10, 0)
        assert(rankings.list[0].id == 2)
        assert(rankings.list[1].id == 1)
    }

    @Test
    fun get_rankings_with_invalid_limit() {
        val exception = assertThrows<AppException> {
            services.getRankings(-1, 0)
        }
        assert(exception.message == "Invalid limit")
    }
}
