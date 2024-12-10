package com.es.proyectgoDinAda_GPS_Tracking.exceptions


class BadRequestException(message: String) : RuntimeException("$DESCRIPTION $message"){
    companion object {
        const val DESCRIPTION = "Bad request exception (400)."
    }
}


class NotFoundException(message: String) : RuntimeException("$DESCRIPTION $message"){
    companion object {
        const val DESCRIPTION = "Not Found Exception (404)."
    }
}


class ConflictException(message: String) : RuntimeException("$DESCRIPTION $message"){
    companion object {
        const val DESCRIPTION = "Conflict Exception (409)."
    }
}

class AlreadyExistsException(message: String) : RuntimeException("$DESCRIPTION $message"){
    companion object {
        const val DESCRIPTION = "Conflict Exception (409). Entity Already Exists."
    }
}

/*

class UnauthorizedException(message: String) : RuntimeException("$DESCRIPTION  $message"){
    companion object {
        const val DESCRIPTION = "Unauthorized Exception (401)."
    }
}
 */





