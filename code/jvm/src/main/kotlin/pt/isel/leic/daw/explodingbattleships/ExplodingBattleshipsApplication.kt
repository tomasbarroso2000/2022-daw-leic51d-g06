package pt.isel.leic.daw.explodingbattleships

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.leic.daw.explodingbattleships.http.pipeline.UserArgumentResolver
import pt.isel.leic.daw.explodingbattleships.http.pipeline.AuthenticationInterceptor
import pt.isel.leic.daw.explodingbattleships.services.Services
import pt.isel.leic.daw.explodingbattleships.utils.Sha256TokenEncoder

@SpringBootApplication
class ExplodingBattleshipsApplication(val services: Services) : WebMvcConfigurer {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun tokenEncoder() = Sha256TokenEncoder()
}

@Configuration
class PipelineConfigurer(
    val authenticationInterceptor: AuthenticationInterceptor,
    val playerArgumentResolver: UserArgumentResolver,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(playerArgumentResolver)
    }
}

fun main(args: Array<String>) {
    runApplication<ExplodingBattleshipsApplication>(*args)
}
