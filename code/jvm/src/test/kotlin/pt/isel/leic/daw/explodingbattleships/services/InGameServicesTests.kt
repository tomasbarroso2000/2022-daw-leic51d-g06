package pt.isel.leic.daw.explodingbattleships.services

import pt.isel.leic.daw.explodingbattleships.data.DataMem

class InGameServicesTests {
    private val data = DataMem()
    private val services = Services(data).inGameServices
}
