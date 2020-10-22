package com.shevaalex.android.rickmortydatabase.utils.networking

data class StateResource (
        val status: Status,
        val message: Message? = null
)


sealed class Status {

    object Loading : Status()

    object Success : Status()

    object Error : Status()

}

sealed class Message {

    object NoInternet : Message()

    object UpdatingDatabase : Message()

    object DbIsUpToDate : Message()

    class ServerError(val statusCode: Int): Message()

    object NetworkError : Message()

    object EmptyResponse : Message()

}