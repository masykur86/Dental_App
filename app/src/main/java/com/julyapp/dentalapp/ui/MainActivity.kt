package com.julyapp.dentalapp.ui

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.julyapp.dentalapp.R
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
    lateinit var viewModel: DentalViewModel
    private lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    val callbackManager = CallbackManager.Factory.create()


    companion object {
        private val TAG = "ClassName"
        private val facebook_permissions = mutableListOf("email", "public_profile")
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
        // Initialize Facebook Login button
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        val viewModelProviderFactory = DentalViewModelProviderFactory(application, auth, this)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(DentalViewModel::class.java)
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
        cekLogin()

//        ss.setSpan(clickableSpan, 24, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
////        binding.tvDaftar.text = ss
////        binding.tvDaftar.movementMethod = LinkMovementMethod.getInstance()
////        binding.tvDaftar.highlightColor = Color.TRANSPARENT
//        form {
//        val pass = binding.editpass.text.toString().trim()
//            inputLayout(R.id.outlinetvEmail, "Email") {
//                isNotEmpty().description("masukkan Email")
//                isEmail().description("Email Salag")
//            }
//            inputLayout(R.id.outlinetvUsername, "Nama") {
//                isNotEmpty().description("masukkan nama")
//
//            }
//            inputLayout(R.id.outlinetvPass, "Password") {
//                isNotEmpty().description("masukkan password")
//
//            }
//            submitWith(R.id.btLogin) {
//                viewModel.registerUser(
//                    binding.editemail.text.toString(),
//                    binding.editpass.text.toString(),
//                    binding.editUsername.text.toString()
//                )
//            }
//        }
//
//
//        binding.Fblogin.setOnClickListener {
//            viewModel.loginManager.logInWithReadPermissions(this,facebook_permissions)
//        }
//
//
//        binding.Glogin.setOnClickListener {
//            viewModel.googleSignIn().also {
//                startActivityForResult(it, R_CODE_GSIGN_IN)
//            }
//
//        }


    }

    private fun cekLogin() {
        val user = auth.currentUser
        if (user != null) {

//            user?.let {
//                if (it.providerData.isEmpty()) {
//                    Toast.makeText(requireContext(), user.toString(), Toast.LENGTH_SHORT).show()
//
//                } else {
//                    for (profile in it.providerData) {
//
//                        Toast.makeText(requireContext(), profile.toString(), Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            }
        } else {
//            Toast.makeText(requireContext(), "terjadi kesalahan", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleOnActivityResult(requestCode, resultCode, data)
//        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

}