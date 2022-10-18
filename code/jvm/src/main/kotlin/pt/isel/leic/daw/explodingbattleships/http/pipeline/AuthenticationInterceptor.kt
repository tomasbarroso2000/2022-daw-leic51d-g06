package pt.isel.leic.daw.explodingbattleships.http.pipeline

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.leic.daw.explodingbattleships.domain.Player
import pt.isel.leic.daw.explodingbattleships.http.doApiTask
import pt.isel.leic.daw.explodingbattleships.http.handleError
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticationInterceptor(
    private val authorizationHeaderProcessor: AuthorizationHeaderProcessor
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod && handler.methodParameters.any { it.parameterType == Player::class.java }) {
            // enforce authentication
            return try {
                val player = authorizationHeaderProcessor.process(request.getHeader(NAME_AUTHORIZATION_HEADER))
                UserArgumentResolver.addPlayerTo(player, request)
                true
            } catch (err: Exception) {
                response.status = 401
                response.addHeader(NAME_WWW_AUTHENTICATE_HEADER, AuthorizationHeaderProcessor.SCHEME)
                false
            }
        }
        return true
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthenticationInterceptor::class.java)
        private const val NAME_AUTHORIZATION_HEADER = "Authorization"
        private const val NAME_WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
    }
}