package com.drew.themoviedatabase

import android.app.UiModeManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.drew.themoviedatabase.screens.Home.HomeScreen
import com.drew.themoviedatabase.screens.Home.MoviesViewModel
import com.drew.themoviedatabase.screens.Profile.LoginViewModel
import com.drew.themoviedatabase.ui.theme.TheMovieDatabaseDarkTheme
import com.drew.themoviedatabase.ui.theme.TheMovieDatabaseTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val loginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {

            TheMovieDatabaseDarkTheme {
                    MovieDatabaseApp(loginViewModel = loginViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        loginViewModel.setApproved(intent.data?.getQueryParameter("approved")?.toBoolean() ?: false)
        loginViewModel.setRequestToken(intent.data?.getQueryParameter("request_token") ?: "")
//        intent.data?.getQueryParameter("request_token")?.let { Log.i("REQUEST_TOKEN", it) }
//        intent.data?.getQueryParameter("approved")?.let { Log.i("APPROVED", it) }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TheMovieDatabaseTheme {
        Greeting("Android")
    }
}
