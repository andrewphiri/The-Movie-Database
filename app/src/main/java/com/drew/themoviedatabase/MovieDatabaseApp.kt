package com.drew.themoviedatabase

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.drew.themoviedatabase.Navigation.MovieNavHost
import com.drew.themoviedatabase.Navigation.NavigationBarRoutes
import com.drew.themoviedatabase.screens.Home.HomeScreen
import com.drew.themoviedatabase.screens.Profile.LoginViewModel
import com.drew.themoviedatabase.screens.Profile.ProfileNavScreen
import com.drew.themoviedatabase.screens.Search.SearchScreen
import com.drew.themoviedatabase.screens.Videos.VideosScreen

@Composable
fun MovieDatabaseApp(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    isConnected: Boolean,
    navController: NavHostController = rememberNavController()
) {
val listState = rememberLazyListState()
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                )
        }
    ) { padding ->
        if (isConnected) {
            MovieNavHost(
                modifier = modifier.padding(padding),
                navController = navController,
                loginViewModel = loginViewModel
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Unfortunately something went wrong. Please check your internet connection."
                )
            }

        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
) {
    val navigationBarRoutes = listOf(
        NavigationBarRoutes("Home", HomeScreen, Icons.Filled.Home),
        NavigationBarRoutes("Search", SearchScreen, Icons.Filled.Search),
        NavigationBarRoutes("Videos", VideosScreen, Icons.Default.PlayArrow),
        NavigationBarRoutes("Profile", ProfileNavScreen, Icons.Outlined.AccountCircle)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isBottomBarVisible = navigationBarRoutes.any { currentDestination?.hasRoute(it.route::class) == true }

        // Show the bottom navigation bar
        NavigationBar {
            navigationBarRoutes.forEach { screen ->
                currentDestination?.hierarchy?.any { it.hasRoute(screen.route::class) }?.let {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.name
                            )
                        },
                        selected = it,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(HomeScreen) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true

                            }
                        }
                    )
                }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieTopAppBar(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
    title: String,
    canNavigateBack: Boolean = false
) {
    if (canNavigateBack) {
        TopAppBar(
            modifier = modifier,
            windowInsets = WindowInsets( 0.dp),
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(),
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate Back"
                    )
                }
            }
        )
    } else {
        TopAppBar(title = { Text(title) }, modifier = modifier,)
    }
}