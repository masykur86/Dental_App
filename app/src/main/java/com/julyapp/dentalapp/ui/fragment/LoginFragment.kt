package com.julyapp.dentalapp.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.julyapp.dentalapp.R
import com.julyapp.dentalapp.databinding.FragmentLoginBinding
import com.julyapp.dentalapp.databinding.FragmentRegisterBinding
import com.julyapp.dentalapp.ui.DentalViewModel
import com.julyapp.dentalapp.ui.MainActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    lateinit var viewModel: DentalViewModel
    lateinit var auth: FirebaseAuth

    companion object {
        private val facebook_permissions = mutableListOf("email", "public_profile")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel
        auth = FirebaseAuth.getInstance()
        cekLogin()



        val ss = SpannableString("Belum Punya Akun? Daftar Disini")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.isFakeBoldText = true
                ds.color = Color.WHITE
            }

        }
        ss.setSpan(clickableSpan, 24, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvDaftar.text = ss
        binding.tvDaftar.movementMethod = LinkMovementMethod.getInstance()
        binding.tvDaftar.highlightColor = Color.TRANSPARENT
        form {
            inputLayout(R.id.outlinetvEmail, "Email") {
                isNotEmpty().description("masukkan Email")
                isEmail().description("Email Salag")
            }
            inputLayout(R.id.outlinetvPass, "Password") {
                isNotEmpty().description("masukkan password")

            }
            submitWith(R.id.btLogin) {
                viewModel.loginUser(
                    binding.editemail.text.toString(),
                    binding.editpass.text.toString()
                )
            }
        }


        binding.Fblogin.setOnClickListener {

            viewModel.loginManager.logInWithReadPermissions(
                this,
                RegisterFragment.facebook_permissions
            )

        }


        binding.Glogin.setOnClickListener {
            viewModel.googleSignIn().also {
                startActivityForResult(it, DentalViewModel.R_CODE_GSIGN_IN)
            }
        }
//        viewModel.currentUser.observe(viewLifecycleOwner, Observer {
//            if (it!=null){
//                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
//            }
//        })

    }

    private fun cekLogin() {
        val user = auth.currentUser
        if (user != null) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
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

    }
}