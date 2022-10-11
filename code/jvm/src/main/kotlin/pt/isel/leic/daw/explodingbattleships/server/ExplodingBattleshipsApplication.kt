package pt.isel.leic.daw.explodingbattleships.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.function.RouterFunction
import pt.isel.leic.daw.explodingbattleships.data.db.DataDb
import pt.isel.leic.daw.explodingbattleships.server.pipeline.argumentresolvers.ClientIpArgumentResolver
import pt.isel.leic.daw.explodingbattleships.server.pipeline.handlerinterceptors.HandlerInterceptor
import pt.isel.leic.daw.explodingbattleships.server.pipeline.messageconverters.CustomOutputModelMessageConverter
import pt.isel.leic.daw.explodingbattleships.server.pipeline.messageconverters.UriToQrCodeMessageConverter
import pt.isel.leic.daw.explodingbattleships.services.Services

@SpringBootApplication
class ExplodingBattleshipsApplication(
    private val exampleHandlerInterceptor: HandlerInterceptor,
    private val clientIpArgumentResolver: ClientIpArgumentResolver,
) : WebMvcConfigurer {

    @Bean
    fun getExampleRoute(): RouterFunction<*> = exampleRouterFunction

    @Bean
    fun getServices() = Services(DataDb())

    @Bean
    fun getExampleWithDependenciesRoute(
        greetingsService: GreetingsService,
    ): RouterFunction<*> = exampleRouterFunctionWithDependencies(greetingsService)

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(clientIpArgumentResolver)
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(0, UriToQrCodeMessageConverter())
        converters.add(0, CustomOutputModelMessageConverter())
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(exampleHandlerInterceptor)
    }
}


fun main(args: Array<String>) {
    runApplication<ExplodingBattleshipsApplication>(*args)
}
