package com.ghost.newsapp.core.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.ghost.newsapp.core.navigation.AppNavigation
import com.ghost.newsapp.core.presentation.ui.theme.NewsAppTheme
import com.ghost.newsapp.core.utils.NetworkConnectivityObserver


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val connectivityObserver = NetworkConnectivityObserver(applicationContext)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NewsAppTheme {
                   AppNavigation(
                       navController,
                       connectivityObserver
                   )
            }
        }
    }
}