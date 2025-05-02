package com.ghost.newsapp.core.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghost.newsapp.core.domain.Article
import com.ghost.newsapp.core.domain.NewsList
import com.ghost.newsapp.core.domain.NewsRepository
import com.ghost.newsapp.core.domain.NewsResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class NewsViewModel(
    private val repository: NewsRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _newsState = MutableStateFlow<NewsResult<NewsList>>(NewsResult.Loading)
    val newsState: StateFlow<NewsResult<NewsList>> = _newsState.asStateFlow()

    private val _articleDetailsState = MutableStateFlow<NewsResult<Article>>(NewsResult.Loading)
    val articleDetailsState: StateFlow<NewsResult<Article>> = _articleDetailsState.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private var nextPage: String? = null

    // Fetch news when the ViewModel is initialized
    init {
        getNews()
    }

    // Fetch News or Articles
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

    // Load more articles
    fun loadMore() {
        // Don't attempt to load if already loading or if no next page
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

    // Fetch article by ID
    fun loadArticleById(articleId: String) {
        viewModelScope.launch(dispatcher) {
            repository.getArticle(articleId)
                .catch { e ->
                    _articleDetailsState.value = NewsResult.Error("Unexpected Error: ${e.localizedMessage}")
                }
                .collect { result ->
                    _articleDetailsState.value = result
                }
        }
    }

    // Set connectivity status and automatically trigger loading when reconnected
    fun setConnectivityStatus(connected: Boolean) {
        _isConnected.value = connected
        if (connected) {
            // Only try to load more articles when we are connected and haven't already loaded them
            if (_newsState.value is NewsResult.Success && !isLoadingMore.value) {
                loadMore()
            }
        }
    }

}


