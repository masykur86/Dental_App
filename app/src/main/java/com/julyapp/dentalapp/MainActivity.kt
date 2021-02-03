package com.julyapp.dentalapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.Login
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.julyapp.dentalapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import java.util.regex.Pattern


    const val R_CODE_GSIGN_IN = 1
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    val callbackManager = CallbackManager.Factory.create()

    companion object {
        private val TAG = "ClassName"
    }
    val PassPattern =
        Pattern.compile(
            "^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
//                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
//                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 4 characters
                    "$"
        );

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // Initialize Facebook Login button
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()

        val ss = SpannableString("Sudah Punya Akun? Masuk Disini")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                Toast.makeText(this@MainActivity, "pindah nnti", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.isFakeBoldText = true
                ds.color = Color.rgb(59, 163, 204)
            }

        }
        ss.setSpan(clickableSpan, 24, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvDaftar.text = ss
        binding.tvDaftar.movementMethod = LinkMovementMethod.getInstance()
        binding.tvDaftar.highlightColor = Color.TRANSPARENT
        binding.btLogin.setOnClickListener {

            if (!validateForm()) {
                return@setOnClickListener
            }
            registerUser(
                binding.editemail.text.toString(),
                binding.editpass.text.toString(),
                binding.editUsername.text.toString()
            )
        }
        
        binding.Fblogin.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager, object :
                FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d(TAG, "facebook:onSuccess:$loginResult")

                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d(TAG, "facebook:onCancel")
                    // ...
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "facebook:onError", error)
                    // ...
                }
            })

        }


        binding.Glogin.setOnClickListener {
            val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.ClietID))
                .requestEmail()
                .build()

            val signInClient = GoogleSignIn.getClient(this, option)
            signInClient.signInIntent.also {
                startActivityForResult(it, R_CODE_GSIGN_IN)
            }

        }



    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === R_CODE_GSIGN_IN) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                googleAuthFirebase(it)
            }
        }
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }


    private fun googleAuthFirebase(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credential).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "login berhasil", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_SHORT).show()
                }

            }

        }

    }
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }

                }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val pass = binding.editpass.text.toString().trim()
        val confPass = binding.editconfpass.text.toString().trim()
        if (pass.isEmpty()) {
            binding.outlinetvPass.error = "Masukkan Password"
            valid = false;
        } else if (confPass.isEmpty()) {
            binding.outlinetvConfPass.error = "Masukkan Konfirmasi Password"
            valid = false;
        } else if (pass != confPass) {
            Toast.makeText(this, "$pass ,$confPass", Toast.LENGTH_SHORT).show()
            binding.outlinetvConfPass.error = "Konfirmasi Password Salah"
            binding.editconfpass.text?.clear()
            valid = false;
        }


        var userName = binding.editUsername.text.toString().trim()
        if (userName.isEmpty()) {
            binding.outlinetvConfPass.error = "Masukkan Username"
            valid = false;
        } else if (userName.length > 15) {
            binding.outlinetvUsername.error = "Username terlalu panjang"
            valid = false;
        }
        val email = binding.editemail.text.toString().trim()
        if (email.isEmpty()) {
            binding.outlinetvEmail.error = "Masukkan Email"
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.outlinetvEmail.error = "Email Salah"
            valid = false;
        }
        return valid
    }


    private fun registerUser(email: String, pass: String, username: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.createUserWithEmailAndPassword(email,pass).await()
                val user = auth.currentUser
                user.let {
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                    user?.updateProfile(profileUpdate)?.await()
                }

                withContext(Dispatchers.Main) {

                    Toast.makeText(this@MainActivity, "pindah nnti", Toast.LENGTH_SHORT).show()
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}