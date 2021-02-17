package com.shevaalex.android.rickmortydatabase.utils.networking

data class StateResource(
        val status: Status,
        val message: Message? = null
)


sealed class Status {

    object Loading : Status() {
        override fun toString(): String {
            return "Status.Loading"
        }
    }

    object Success : Status() {
        override fun toString(): String {
            return "Status.Success"
        }
    }

    object Error : Status() {
        override fun toString(): String {
            return "Status.Error"
        }
    }

}

sealed class Message {

    object NoInternet : Message()

    object DbIsUpToDate : Message()

    data class ServerError(val statusCode: Int) : Message()

    object NetworkError : Message()

    object EmptyResponse : Message()

}