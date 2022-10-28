package pt.isel.leic.daw.explodingbattleships.http.pipeline

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.leic.daw.explodingbattleships.domain.User
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Intercepts the request and verifies if it's authenticated
 */
@Component
class AuthenticationInterceptor(
    private val authorizationHeaderProcessor: AuthorizationHeaderProcessor
) : HandlerInterceptor {

    /**
     * Process user authentication in request
     * @param request
     * @param response
     * @param handler handler used in the request
     * @return true if the authentication is valid or if its not needed any for the type of request
     */
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod && handler.methodParameters.any { it.parameterType == User::class.java }) {
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