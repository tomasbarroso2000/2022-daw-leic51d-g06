package pt.isel.leic.daw.explodingbattleships.server

import org.springframework.stereotype.Component

@Component
class DefaultGreetingsService : GreetingsService {
    override val greetings = "Hello"
}
