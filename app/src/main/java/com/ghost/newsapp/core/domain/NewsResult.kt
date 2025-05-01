package com.ghost.newsapp.core.domain

sealed class NewsResult<out T>(
) {
    data object Loading: NewsResult<Nothing>()
    data class Success<T>(val data: T?) : NewsResult<T>()
    class Error<T>(val error: String?) : NewsResult<T>()
}