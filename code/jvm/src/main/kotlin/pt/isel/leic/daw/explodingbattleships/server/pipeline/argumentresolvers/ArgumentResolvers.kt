package pt.isel.leic.daw.explodingbattleships.server.pipeline.argumentresolvers

import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import pt.isel.leic.daw.explodingbattleships.server.ClientIp
import javax.servlet.http.HttpServletRequest

@Component
class ClientIpArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter) =
        parameter.parameterType == ClientIp::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val request: HttpServletRequest =
            webRequest.getNativeRequest(HttpServletRequest::class.java)!!
        return ClientIp(ipAddress = request.remoteAddr)
    }
}
