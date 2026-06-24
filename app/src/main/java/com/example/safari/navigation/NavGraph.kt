package com.example.safari.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safari.browser.BrowserViewModel
import com.example.safari.model.Routes
import com.example.safari.ui.screens.BrowserRootScreen

@Composable
fun SafariNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: BrowserViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.BROWSER,
        modifier = modifier
    ) {
        composable(Routes.BROWSER) {
            // BrowserRootScreen wraps entire UI in LiquidGlassRoot
            // so all glass components can sample the real backdrop
            BrowserRootScreen(viewModel = viewModel)
        }
    }
}
