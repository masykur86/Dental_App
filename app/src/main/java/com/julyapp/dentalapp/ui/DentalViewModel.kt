package com.julyapp.dentalapp.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.julyapp.dentalapp.DentalApplication
import com.julyapp.dentalapp.R
import com.julyapp.dentalapp.data.Result
import com.julyapp.dentalapp.models.Users
import com.julyapp.dentalapp.ui.fragment.HomeFragment
import com.julyapp.dentalapp.utils.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException


class DentalViewModel(app: Application, val auth: FirebaseAuth, val context: Context) :
    AndroidViewModel(app) {
    companion object {
        const val R_CODE_GSIGN_IN = 1
        const val GOOGLE_PRIVATE_CLIENT_ID =
            "802918450737-jesugea4dbh4o3gh0j4u9nfnp42sbkbn.apps.googleusercontent.com"
    }

    val currentUser: MutableLiveData<FirebaseUser> = MutableLiveData()


    val loginManager: LoginManager = LoginManager.getInstance()
    private val mCallbackManager = CallbackManager.Factory.create()
    private val mFacebookCallback = object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult?) {
            val credential = FacebookAuthProvider.getCredential(result?.accessToken?.token!!)
            handleFacebookCredential(credential)
        }


        override fun onError(error: FacebookException?) {

        }

        override fun onCancel() {

        }
    }

    init {
        loginManager.registerCallback(mCallbackManager, mFacebookCallback)
        cekLogin()
    }

    private fun cekLogin() {
        val user = auth.currentUser
        if (user != null) {
                currentUser.postValue(user)
        } else {
//            Toast.makeText(requireContext(), "terjadi kesalahan", Toast.LENGTH_SHORT).show()

        }
    }

    private fun handleFacebookCredential(authCredential: AuthCredential) {

            viewModelScope.launch {
                safeApiCall { Result.Success(signInWithCredential(authCredential)!!) }.also {
                    if (it is Result.Success && it.data.user != null) {
                        Toast.makeText(context, "berhasil", Toast.LENGTH_SHORT).show()
                        currentUser.postValue(it.data.user)


                    } else if (it is Result.Error) {

                    }
                }
            }


    }

    val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(GOOGLE_PRIVATE_CLIENT_ID)
        .requestEmail()
        .build()

    private val mGoogleSignClient by lazy {
        GoogleSignIn.getClient(app, option)
    }

    fun googleSignIn() = mGoogleSignClient.signInIntent


    fun registerUser(email: String, pass: String, username: String) {
        try {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.createUserWithEmailAndPassword(email, pass).await()
                        val user = auth.currentUser
                        user.let {
                            val profileUpdate = UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build()
                            user?.updateProfile(profileUpdate)?.await()
                        }
                        currentUser.postValue(user)

                        withContext(Dispatchers.Main) {
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }


        } catch (t: Throwable) {
            when (t) {
                is IOException -> Toast.makeText(context, "Tidak ada jaringan", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    }


    fun loginUser(email: String, pass: String) {


                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.signInWithEmailAndPassword(email, pass).await()
                        val user = auth.currentUser
                        currentUser.postValue(user)

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                        }

                    }
                }

    }

    private fun handleGoogleSignInResult(data: Intent) {
        viewModelScope.launch {
            safeApiCall {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).await()
                val authResult =
                    signInWithCredential(
                        GoogleAuthProvider.getCredential(
                            account.idToken,
                            null
                        )
                    )!!
                Result.Success(authResult)
            }.also {
                if (it is Result.Success && it.data.user != null) {
                    currentUser.postValue(it.data.user)

                } else {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()

                }
            }
        }


    }


    private suspend fun signInWithCredential(authCredential: AuthCredential): AuthResult? {
        return auth.signInWithCredential(authCredential).await()
    }

    fun handleOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == com.julyapp.dentalapp.ui.R_CODE_GSIGN_IN && data != null) {
            handleGoogleSignInResult(data)
        } else if (mCallbackManager.onActivityResult(requestCode, resultCode, data)
        )
            println("Result should be handled")
    }


}

