package com.drew.themoviedatabase

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.drew.themoviedatabase.Navigation.MovieNavHost

@Composable
fun MovieDatabaseApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    MovieNavHost(
        modifier = modifier,
        navController = navController
    )

}