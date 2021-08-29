package io.github.pikokr.bucket.exception

class InvalidPluginException : RuntimeException {
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)
}