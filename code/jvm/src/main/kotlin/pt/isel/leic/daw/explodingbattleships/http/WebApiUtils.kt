package pt.isel.leic.daw.explodingbattleships.http

import org.springframework.http.ResponseEntity
import pt.isel.leic.daw.explodingbattleships.services.utils.AppException
import pt.isel.leic.daw.explodingbattleships.services.utils.AppExceptionStatus
import javax.servlet.http.HttpServletRequest

const val OK = 200
const val CREATED = 201
const val ACCEPTED = 202

const val MOVED_PERMANENTLY = 301
const val NOT_MODIFIED = 304

const val BAD_REQUEST = 400
const val UNAUTHORIZED = 401
const val FORBIDDEN = 403
const val NOT_FOUND = 404
const val INTERNAL_SERVER_ERROR = 500
const val NOT_IMPLEMENTED = 501

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

data class ErrorResponse(val msg: String)

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
            INTERNAL_SERVER_ERROR,
            ErrorResponse("Something went wrong")
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
        .header("content-type", "application/json")
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
            UNAUTHORIZED, ErrorResponse(message)
        )
        AppExceptionStatus.BAD_REQUEST -> makeResponse(
            BAD_REQUEST,ErrorResponse(message)
        )
        AppExceptionStatus.NOT_FOUND -> makeResponse(
            NOT_FOUND, ErrorResponse(message)
        )
        AppExceptionStatus.INTERNAL -> makeResponse(
            INTERNAL_SERVER_ERROR, ErrorResponse(message)
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