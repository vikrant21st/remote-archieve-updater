package jetbrains.compose.classfileupdator.utils

sealed class Try<out R, out E> {
    fun <T> handle(handler: (Try<R, E>) -> T): T {
        return handler(this)
    }
}

data class Success<out T>(val value: T) : Try<T, Nothing>()

data class Failure<out T>(val error: T) : Try<Nothing, T>()
