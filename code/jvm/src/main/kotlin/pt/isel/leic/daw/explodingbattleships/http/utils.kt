package pt.isel.leic.daw.explodingbattleships.http

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus
import javax.servlet.http.HttpServletRequest
import pt.isel.leic.daw.explodingbattleships.infra.siren

val APPLICATION_SIREN = MediaType.parseMediaType("application/vnd.siren+json")

/**
 * Calls a task using the same try-catch block
 * @param task the task to be called
 * @return the response
 */
fun doApiTask(task: () -> ResponseEntity<*>): ResponseEntity<*> {
    return try {
        task()
    } catch (error: Exception) {
        handleError(error)
    }
}

data class ErrorResponse(
    val type: String = "",
    val title: String,
    val status: Int,
    val detail: String = "",
    val instance: String = ""
)

/**
 * Handles the errors caught in the Web-Api
 * @param error the exception thrown
 * @return a status response for the user to see
 */
fun handleError(error: Exception): ResponseEntity<ErrorResponse> {
    return if (error is AppException)
        onAppException(error)
    else
        makeResponse(
            Errors.INTERNAL_SERVER_ERROR,
            ErrorResponse(title = "Something went wrong", status = Errors.INTERNAL_SERVER_ERROR)
        )
}

/**
 * Fabricate a response with the given status and response body
 * @param statusCode the status of the [ResponseEntity]
 * @param body the body of the [ResponseEntity]
 */
fun <T> makeResponse(statusCode: Int, body: T) =
    ResponseEntity
        .status(statusCode)
        .header("content-type", "application/problem+json")
        .body(body)

/**
 * Handles any [AppException] that was thrown
 * by the Services module
 * @param error the exception thrown
 * @return a status response for the user to see
 */
fun onAppException(error: AppException): ResponseEntity<ErrorResponse> {
    val message = error.message ?: "¯\\_(ツ)_/¯"
    return when (error.status) {
        AppExceptionStatus.UNAUTHORIZED -> makeResponse(
            Errors.UNAUTHORIZED, ErrorResponse(title = message, status = Errors.UNAUTHORIZED)
        )
        AppExceptionStatus.BAD_REQUEST -> makeResponse(
            Errors.BAD_REQUEST, ErrorResponse(title = message, status = Errors.BAD_REQUEST)
        )
        AppExceptionStatus.NOT_FOUND -> makeResponse(
            Errors.NOT_FOUND, ErrorResponse(title = message, status = Errors.NOT_FOUND)
        )
        AppExceptionStatus.INTERNAL -> makeResponse(
            Errors.INTERNAL_SERVER_ERROR, ErrorResponse(title = message, status = Errors.INTERNAL_SERVER_ERROR)
        )
    }
}

/**
 * Extension function of Request that gets the user token from the Authorization header
 * @return the token
 */
val HttpServletRequest.token: String?
    get() {
        val auth = this.getHeader("Authorization")
        if (auth != null) {
            val authData = auth.trim().split(' ')
            if (authData[0] == "Bearer") {
                return authData[1]
            }
        }
        return null
    }

fun getTokenFromAuthorization(authorization: String?): String? {
    if (authorization != null) {
        val authData = authorization.trim().split(' ')
        if (authData[0].lowercase() == "bearer") {
            return authData[1]
        }
    }
    return null
}