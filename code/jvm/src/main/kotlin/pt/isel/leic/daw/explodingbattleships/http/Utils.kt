package pt.isel.leic.daw.explodingbattleships.http

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import pt.isel.leic.daw.explodingbattleships.http.models.input.UserTokenInputModel
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus
import pt.isel.leic.daw.explodingbattleships.services.utils.logger
import java.util.Base64

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

/**
 * Represents an error response
 * @param type the error type
 * @param title the error response title
 * @param detail the error details
 * @param instance the error instance
 */
data class ErrorResponse(
    val type: String = "",
    val title: String,
    val detail: String = "",
    val instance: String = ""
)

/**
 * Handles the errors caught in the Web-Api
 * @param error the exception thrown
 * @return a status response for the user to see
 */
fun handleError(error: Exception): ResponseEntity<ErrorResponse> {
    return if (error is AppException) {
        onAppException(error)
    } else {
        logger.warn(error.message)
        makeProblemResponse(
            Errors.INTERNAL_SERVER_ERROR,
            ErrorResponse(title = "Something went wrong")
        )
    }
}

/**
 * Fabricates an error response with the given status and response body
 * @param statusCode the status of the [ResponseEntity]
 * @param body the body of the [ResponseEntity]
 */
fun <T> makeProblemResponse(statusCode: Int, body: T) =
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
        AppExceptionStatus.UNAUTHORIZED -> makeProblemResponse(
            Errors.UNAUTHORIZED,
            ErrorResponse(title = message)
        )
        AppExceptionStatus.BAD_REQUEST -> makeProblemResponse(
            Errors.BAD_REQUEST,
            ErrorResponse(title = message)
        )
        AppExceptionStatus.NOT_FOUND -> makeProblemResponse(
            Errors.NOT_FOUND,
            ErrorResponse(title = message)
        )
        AppExceptionStatus.INTERNAL -> makeProblemResponse(
            Errors.INTERNAL_SERVER_ERROR,
            ErrorResponse(title = message)
        )
    }
}

/**
 * Gets the user token from the Authorization header
 * @param authorization the authorization header
 * @return the token
 */
fun getTokenFromAuthorization(authorization: String?): String? {
    if (authorization != null) {
        val authData = authorization.trim().split(' ')
        if (authData[0].lowercase() == "bearer") {
            return authData[1]
        }
    }
    return null
}

/**
 * Used to decode Authorization header
 */
val base64Decoder: Base64.Decoder = Base64.getDecoder()

/**
 * Gets the user credentials from the Authorization header
 * @return the token
 */
fun getCredentialsFromAuthorization(authorization: String?): UserTokenInputModel? {
    if (authorization != null) {
        val authData = authorization.trim().split(' ')
        if (authData[0].lowercase() == "basic") {
            val decodedData = String(base64Decoder.decode(authData[1]))
            val credentials = decodedData.split(':')
            return UserTokenInputModel(credentials[0], credentials[1])
        }
    }
    return null
} // use when implementing basic authentication for creating a token