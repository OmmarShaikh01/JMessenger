package org.jam.jmessenger.data.db

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class Loading(val loading: Boolean) : Result<Nothing>()
}