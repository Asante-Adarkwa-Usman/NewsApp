package com.ghost.newsapp.core.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.ghost.newsapp.core.domain.NewsResult
import com.ghost.newsapp.core.presentation.vm.NewsViewModel
import com.ghost.newsapp.core.utils.ConnectivityObserver


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(
    viewModel: NewsViewModel,
    navController: NavController,
    connectivityObserver: ConnectivityObserver
) {
    val lifecycleOwner = LocalLifecycleOwner.current.lifecycle
    val newsState by viewModel.newsState.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val nestedScrollConnection = scrollBehavior.nestedScrollConnection
    val snackbarHostState = remember { SnackbarHostState() }
    var triedLoadingWhileOffline by remember { mutableStateOf(false) }

    // Collect connectivity changes and update ViewModel
    LaunchedEffect(Unit) {
        connectivityObserver.observe()
            .flowWithLifecycle(lifecycleOwner, Lifecycle.State.STARTED)
            .collect { isConnected ->
                viewModel.setConnectivityStatus(isConnected)

                // Reset triedLoadingWhileOffline flag when the connection is restored
                if (isConnected) {
                    triedLoadingWhileOffline = false
                }
            }
    }

    // Show snackbar when no connection and user scrolls to bottom
    LaunchedEffect(isConnected) {
        // Show the snackbar only when there's no connection and it's the first time trying to load more while offline
        if (!isConnected && !triedLoadingWhileOffline) {
            snackbarHostState.showSnackbar("No internet connection. Load more news later ...")
            triedLoadingWhileOffline = true
        }
    }

    when (newsState) {
        is NewsResult.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is NewsResult.Error -> {
            val message = (newsState as NewsResult.Error).error
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $message", color = Color.Red)
            }
        }

        is NewsResult.Success -> {
            val articles = (newsState as NewsResult.Success).data?.articles ?: emptyList()

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        title = { Text("News") },
                        scrollBehavior = scrollBehavior
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    itemsIndexed(articles) { index, article ->
                        ArticleItem(article) { articleId ->
                            navController.navigate("article_detail/$articleId")
                        }

                        if (index == articles.lastIndex && !isLoadingMore && isConnected) {
                            LaunchedEffect(Unit) {
                                viewModel.loadMore()
                            }
                        }
                    }

                    if (isLoadingMore) {
                        item {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}




