package com.drew.themoviedatabase.repository.login

import android.util.Log
import com.drew.themoviedatabase.Network.API_KEY
import com.drew.themoviedatabase.Network.CreateSessionResponse
import com.drew.themoviedatabase.Network.LoginApiService
import com.drew.themoviedatabase.Network.RequestTokenResponse
import com.drew.themoviedatabase.POJO.DeleteSession
import com.drew.themoviedatabase.POJO.UserProfile
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val loginService: LoginApiService
) {
    suspend fun requestToken() : RequestTokenResponse? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = loginService.createRequestToken(apiKey = API_KEY)?.execute()
                        Log.d("LOGIN_REPOSITORY", "Response: ${response?.body()}")
                        if (response?.isSuccessful == true) {
                            response.body()
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun createSessionID(requestToken: String) : CreateSessionResponse? {
        val jsonBody = JsonObject().apply {
            addProperty("request_token", requestToken)
        }
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = loginService.createSessionID(apiKey = API_KEY, jsonBody = jsonBody )?.execute()

                        if (response?.isSuccessful == true) {
                            response.body()
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getAccountID(sessionId: String) : UserProfile? {

        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = loginService.getAccountId(apiKey = API_KEY, sessionId = sessionId )?.execute()

                        if (response?.isSuccessful == true) {
                            response.body()
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteSessionID(sessionId: String) : Response<DeleteSession>? {
        val jsonBody = JsonObject().apply {
            addProperty("session_id", sessionId)
        }
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = loginService.deleteSession(apiKey = API_KEY, jsonBody = jsonBody )?.execute()

                        if (response?.isSuccessful == true) {
                            response
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}