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


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NewsAppTheme {
                   AppNavigation(navController)
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun AppPreview() {
    NewsAppTheme {
        AppNavigation(navController = rememberNavController())
    }
}