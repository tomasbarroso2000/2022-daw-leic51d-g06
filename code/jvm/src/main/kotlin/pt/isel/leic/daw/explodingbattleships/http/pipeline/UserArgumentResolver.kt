package pt.isel.leic.daw.explodingbattleships.http.pipeline

import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import pt.isel.leic.daw.explodingbattleships.domain.Player
import javax.servlet.http.HttpServletRequest

@Component
class UserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter) = parameter.parameterType == Player::class.java

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

        fun addPlayerTo(player: Player, request: HttpServletRequest) {
            return request.setAttribute(KEY, player)
        }

        fun getPlayerFrom(request: HttpServletRequest): Player? {
            return request.getAttribute(KEY)?.let {
                it as? Player
            }
        }
    }
}