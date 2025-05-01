package com.ghost.newsapp.core.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghost.newsapp.core.domain.NewsList
import com.ghost.newsapp.core.domain.NewsRepository
import com.ghost.newsapp.core.domain.NewsResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class NewsViewModel(
    private val repository: NewsRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _newsState = MutableStateFlow<NewsResult<NewsList>>(NewsResult.Loading)
    val newsState: StateFlow<NewsResult<NewsList>> = _newsState.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var nextPage: String? = null

    init {
        getNews()
    }

    fun getNews() {
        viewModelScope.launch(dispatcher) {
            repository.getNews().collect { result ->
                _newsState.value = result
                if (result is NewsResult.Success) {
                    nextPage = result.data?.nextPage
                }
            }
        }
    }

    fun loadMore() {
        val page = nextPage ?: return
        if (_isLoadingMore.value) return

        viewModelScope.launch(dispatcher) {
            _isLoadingMore.value = true
            repository.paginate(page).collect { result ->
                if (result is NewsResult.Success) {
                    val current = (_newsState.value as? NewsResult.Success)?.data?.articles ?: emptyList()
                    val newArticles = result.data?.articles ?: emptyList()
                    val combined = current + newArticles
                    nextPage = result.data?.nextPage
                    _newsState.value = NewsResult.Success(NewsList(nextPage, combined))
                }
            }
            _isLoadingMore.value = false
        }
    }
}

