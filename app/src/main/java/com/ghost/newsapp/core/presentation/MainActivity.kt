package com.ghost.newsapp.core.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ghost.newsapp.core.presentation.ui.NewsScreen
import com.ghost.newsapp.core.presentation.ui.theme.NewsAppTheme
import com.ghost.newsapp.core.presentation.vm.NewsViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
             val viewModel:  NewsViewModel = koinViewModel()
            NewsAppTheme {
                   NewsScreen(viewModel)
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val viewModel: NewsViewModel = koinViewModel()
    NewsAppTheme {
        NewsScreen(viewModel= viewModel)
    }
}