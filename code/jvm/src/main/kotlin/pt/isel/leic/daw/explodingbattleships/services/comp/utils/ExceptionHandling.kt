package pt.isel.leic.daw.explodingbattleships.services.comp.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.DataException
import java.sql.SQLException

private const val UNIQUE_VIOLATION = "23505"
private const val CHECK_VIOLATION = "23514"

val logger: Logger = LoggerFactory.getLogger(
    "pt.isel.daw.explodingbattleships.services.comp.ExceptionHandling"
)

/**
 * Represents an app exception
 * @property message the exception message
 * @property status the status of the exception
 */
data class AppException(
    override val message: String? = null,
    val status: AppExceptionStatus = AppExceptionStatus.INTERNAL
) : Exception() {
    init {
        logger.warn(message)
    }
}

/**
 * Represents an app exception status
 * used to later convert the exception to
 * the correct HTTP response
 */
enum class AppExceptionStatus {
    UNAUTHORIZED,
    BAD_REQUEST,
    NOT_FOUND,
    INTERNAL
}

/**
 * Transforms a data exception into an [AppException]
 * for it to be interpreted by the WebApi module
 * @param error the [Exception] that was thrown
 * @return the new [AppException]
 */
fun handleDataError(error: Exception): Exception {
    return when (error) {
        is SQLException -> {
            handleSQLException(error)
        }
        is DataException -> {
            AppException(error.message, AppExceptionStatus.BAD_REQUEST)
        }
        else -> {
            logger.warn(error.message)
            error
        }
    }
}

/**
 * Transforms a SQL exception into an [AppException]
 * for it to be interpreted by the WebApi module
 * @param error the [SQLException] that was thrown
 * @return the new [AppException]
 */
fun handleSQLException(error: SQLException): Exception {
    return when {
        isUniqueViolation(error) -> {
            AppException(buildUniqueViolationMessage(error.message), AppExceptionStatus.BAD_REQUEST)
        }
        isCheckViolation(error) -> {
            AppException(buildCheckViolationMessage(error.message), AppExceptionStatus.BAD_REQUEST)
        }
        else -> {
            AppException("Something went wrong", AppExceptionStatus.INTERNAL)
        }
    }
}

/**
 * Checks if [SQLException] represents
 * a unique violation in the database
 * @param error the [SQLException] that was thrown
 * @return true if it is a unique violation
 */
fun isUniqueViolation(error: SQLException) =
    error.sqlState == UNIQUE_VIOLATION

/**
 * Checks if [SQLException] represents
 * a check violation in the database
 * @param error the [SQLException] that was thrown
 * @return true if it is a check violation
 */
fun isCheckViolation(error: SQLException) =
    error.sqlState == CHECK_VIOLATION

/**
 * Builds an error message for
 * unique violation errors
 * @param message the original [SQLException] message
 * @return the new error message
 */
fun buildUniqueViolationMessage(message: String?): String? {
    val detail = message?.split("Detail: ")?.get(1) ?: return message
    val column = detail.substringAfter('(').substringBefore(')')
    val value = detail.substringAfterLast('(').substringBefore(')')
    return "The $column $value is already in use"
}

/**
 * Builds an error message for
 * check violation errors
 * @param message the original [SQLException] message
 * @return the new error message
 */
fun buildCheckViolationMessage(message: String?): String? {
    val column = message?.substringAfter('_')?.substringBeforeLast('_') ?: return message
    return "Invalid $column"
}
