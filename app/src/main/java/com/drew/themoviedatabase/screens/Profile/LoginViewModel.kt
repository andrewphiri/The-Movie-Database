package com.drew.themoviedatabase.screens.Profile

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drew.themoviedatabase.MoviesApplication
import com.drew.themoviedatabase.data.model.UserProfile
import com.drew.themoviedatabase.data.repository.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    val application: MoviesApplication
) : ViewModel() {

    private val _requestToken = MutableStateFlow("")
    val requestToken = _requestToken.asStateFlow()

    private val _approved = MutableStateFlow(false)
    val approved: StateFlow<Boolean> = _approved

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    fun setUserProfile(profile: UserProfile?) {
        _userProfile.value = profile
    }

    fun setApproved(approved: Boolean) {
        _approved.value = approved
        Log.d("LoginViewModel", "Approved: $approved")
    }

    fun setRequestToken(requestToken: String) {
        _requestToken.value = requestToken
        Log.d("LoginViewModel", "Request Token: $requestToken")
    }

    fun authenticateUser(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        viewModelScope.launch {
            val requestToken = getRequestToken()
            Log.d("LoginViewModel", "Request Token: $requestToken")
            val myAppURL = "mdbapp://www.drew.mdb.com"
            val url = "https://www.themoviedb.org/authenticate/$requestToken?redirect_to=${myAppURL}"
            val launcherIntent = Intent(Intent.ACTION_VIEW, url.toUri())
            launcher.launch(launcherIntent)
        }
    }

    suspend fun requestSessionID(requestToken: String) : String? {
        val sessionID = loginRepository.createSessionID(requestToken)
        return if (sessionID?.success == true) sessionID.session_id else null
    }

    suspend fun getAccountID(sessionId: String) : UserProfile? {
        return loginRepository.getAccountID(sessionId)
    }

    suspend fun getRequestToken() : String? {
        return loginRepository.requestToken()?.request_token
    }

    suspend fun deleteSession(sessionId: String) : Response<com.drew.themoviedatabase.data.model.DeleteSession>? {
        return loginRepository.deleteSessionID(sessionId)
    }


    fun requestUrl(requestToken: String?) : String {
        val myAppURL = "mdbapp://www.drew.mdb.com"
        return "https://www.themoviedb.org/authenticate/$requestToken?redirect_to=${myAppURL}"
    }

}