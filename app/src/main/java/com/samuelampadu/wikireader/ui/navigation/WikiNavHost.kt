package com.samuelampadu.wikireader.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.samuelampadu.wikireader.ui.search.SearchRoute

private const val SEARCH_ROUTE = "search"

@Composable
fun WikiNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = SEARCH_ROUTE) {
        composable(SEARCH_ROUTE) {
            SearchRoute(onArticleClick = { _, _ -> })
        }
    }
}
