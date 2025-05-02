package com.ghost.newsapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ghost.newsapp.core.presentation.ui.ArticleDetailScreen
import com.ghost.newsapp.core.presentation.ui.ArticleScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(navController: NavHostController){
    NavHost(navController = navController, startDestination = "article") {
        composable("article") {
            ArticleScreen(navController = navController, viewModel = koinViewModel())
        }

        composable(
            "article_detail/{articleId}",
            arguments = listOf(navArgument("articleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
            ArticleDetailScreen(articleId = articleId, viewModel = koinViewModel(), navController= navController)
        }
    }
}


