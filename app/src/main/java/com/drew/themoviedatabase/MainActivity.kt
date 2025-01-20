package com.drew.themoviedatabase

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drew.themoviedatabase.data.model.YoutubeChannels
import com.drew.themoviedatabase.prefs.UserPreferencesViewModel
import com.drew.themoviedatabase.screens.Profile.LoginViewModel
import com.drew.themoviedatabase.screens.Profile.UserViewModel
import com.drew.themoviedatabase.screens.Videos.VideosViewModel
import com.drew.themoviedatabase.ui.theme.TheMovieDatabaseDarkTheme
import com.drew.themoviedatabase.ui.theme.TheMovieDatabaseTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val loginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    private val videoViewModel by lazy {
        ViewModelProvider(this)[VideosViewModel::class.java]
    }

    private val userPrefsViewModel by lazy {
        ViewModelProvider(this)[UserPreferencesViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        //enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val connectivityViewModel : ConnectivityViewModel = hiltViewModel()
            val isConnected by connectivityViewModel.isConnected.collectAsStateWithLifecycle()
            Log.d("MainActivity", "isConnected: $isConnected")

            val isDataInserted by userPrefsViewModel.isDataInserted.collectAsState()
            //Log.d("MainActivity", "isDataInserted: $isDataInserted")
            insertChannels(isDataInserted)

            TheMovieDatabaseDarkTheme {

                    MovieDatabaseApp(
                        isConnected = isConnected,
                        loginViewModel = loginViewModel)
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

    fun insertChannels(isDataInserted: Boolean) {
        val youtubeChannelsList = listOf(
            YoutubeChannels(channelName = "Netflix", channelID = "UCWOA1ZGywLbqmigxE4Qlvuw", isChannelEnabled = true),
            YoutubeChannels(channelName = "Warner BrosPictures", channelID = "UCjmJDM5pRKbUlVIzDYYWb6g", isChannelEnabled = true),
            YoutubeChannels(channelName = "Walt Disney", channelID = "UCuaFvcY4MhZY3U43mMt1dYQ", isChannelEnabled = true),
            YoutubeChannels(channelName = "HBO", channelID = "UCVTQuK2CaWaTgSsoNkn5AiQ", isChannelEnabled = false),
            YoutubeChannels(channelName = "Movieclips", channelID = "UC3gNmTGu-TTbFPpfSs5kNkg", isChannelEnabled = false),
            YoutubeChannels(channelName = "Rotten Tomatoes Trailers", channelID = "UCi8e0iOVk1fEOogdfu4YgfA", isChannelEnabled = false),
            YoutubeChannels(channelName = "Rotten Tomatoes TV", channelID = "UCz1GPotHecuLngiLuY739QQ", isChannelEnabled = false),
            YoutubeChannels(channelName = "Binge Society", channelID = "UCOo_v3eVbfET7_zi2KLOP9g", isChannelEnabled = false)
        )
        if (!isDataInserted) {
            for (channel in youtubeChannelsList) {
                videoViewModel.insertChannel(channel)
            }
            userPrefsViewModel.updateIsDataInserted(true)
        }
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
