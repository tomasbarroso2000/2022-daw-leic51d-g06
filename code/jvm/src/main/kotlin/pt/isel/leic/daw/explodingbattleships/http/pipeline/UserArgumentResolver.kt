package pt.isel.leic.daw.explodingbattleships.http.pipeline

import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import pt.isel.leic.daw.explodingbattleships.domain.User
import javax.servlet.http.HttpServletRequest

@Component
class UserArgumentResolver : HandlerMethodArgumentResolver {

    /**
     * Checks if a method parameter type is the same type as User
     * @param parameter method parameter
     * @returns true if method parameter type is the same type as User
     */
    override fun supportsParameter(parameter: MethodParameter) = parameter.parameterType == User::class.java

    /**
     * Gets user from request or throws IllegalStateException otherwise
     * @param parameter
     * @param mavContainer
     * @param webRequest
     * @param binderFactory
     */
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw IllegalStateException("TODO")
        return getPlayerFrom(request) ?: throw IllegalStateException("TODO")
    }

    companion object {
        private const val KEY = "UserArgumentResolver"

        /**
         * Associates UserArgumentResolver with a user
         * @param player user
         * @param request
         */
        fun addPlayerTo(player: User, request: HttpServletRequest) {
            return request.setAttribute(KEY, player)
        }

        /**
         * Gets User present in UserArgumentResolver
         * @param request
         * @returns user or null if it's not present in UserArgumentResolver
         */
        fun getPlayerFrom(request: HttpServletRequest): User? {
            return request.getAttribute(KEY)?.let {
                it as? User
            }
        }
    }
}