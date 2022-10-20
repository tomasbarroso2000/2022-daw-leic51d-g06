package pt.isel.leic.daw.explodingbattleships.http

object Errors {
    // Redirection Responses
    const val MOVED_PERMANENTLY = 301
    const val NOT_MODIFIED = 304

    // Client Error Responses
    const val BAD_REQUEST = 400
    const val UNAUTHORIZED = 401
    const val FORBIDDEN = 403
    const val NOT_FOUND = 404

    // Server Error Messages
    const val INTERNAL_SERVER_ERROR = 500
    const val NOT_IMPLEMENTED = 501
}

object Successes {
    // Successfull Responses
    const val OK = 200
    const val CREATED = 201
    const val ACCEPTED = 202
}