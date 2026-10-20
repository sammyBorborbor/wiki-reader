package com.samuelampadu.wikireader.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samuelampadu.wikireader.ui.article.ArticleRoute
import com.samuelampadu.wikireader.ui.search.SearchRoute

object ArticleArgs {
    const val LANG = "lang"
    const val TITLE = "title"
}

private const val SEARCH_ROUTE = "search"
private const val ARTICLE_ROUTE = "article/{${ArticleArgs.LANG}}/{${ArticleArgs.TITLE}}"

@Composable
fun WikiNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = SEARCH_ROUTE) {
        composable(SEARCH_ROUTE) {
            SearchRoute(
                onArticleClick = { language, title ->
                    navController.navigate("article/${language.code}/${Uri.encode(title)}")
                },
            )
        }
        composable(
            route = ARTICLE_ROUTE,
            arguments = listOf(
                navArgument(ArticleArgs.LANG) { type = NavType.StringType },
                navArgument(ArticleArgs.TITLE) { type = NavType.StringType },
            ),
        ) {
            ArticleRoute(onBack = { navController.popBackStack() })
        }
    }
}
