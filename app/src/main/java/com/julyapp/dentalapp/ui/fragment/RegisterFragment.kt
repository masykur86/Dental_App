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
import com.julyapp.dentalapp.R
import com.julyapp.dentalapp.databinding.FragmentRegisterBinding
import com.julyapp.dentalapp.ui.DentalViewModel
import com.julyapp.dentalapp.ui.DentalViewModel.Companion.R_CODE_GSIGN_IN
import com.julyapp.dentalapp.ui.MainActivity


class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding
    lateinit var viewModel: DentalViewModel
    lateinit var auth: FirebaseAuth

    companion object
    {
        val facebook_permissions = mutableListOf("email", "public_profile")

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel
//        viewModel.currentUser.observe(viewLifecycleOwner, Observer {
//            if (it !=null){
//                findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
//            }
//        })
        val ss = SpannableString("Sudah Punya Akun? Masuk Disini")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.isFakeBoldText = true
//                ds.color = Color.rgb(59, 163, 204)
                ds.color = Color.WHITE
            }

        }
        ss.setSpan(clickableSpan, 24, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvDaftar.text = ss
        binding.tvDaftar.movementMethod = LinkMovementMethod.getInstance()
        binding.tvDaftar.highlightColor = Color.TRANSPARENT

        form {
        val pass = binding.editpass.text.toString().trim()
            inputLayout(R.id.outlinetvEmail, "Email") {
                isNotEmpty().description("masukkan Email")
                isEmail().description("Email Salag")
            }
            inputLayout(R.id.outlinetvUsername, "Nama") {
                isNotEmpty().description("masukkan nama")

            }
            inputLayout(R.id.outlinetvPass, "Password") {
                isNotEmpty().description("masukkan password")

            }
            submitWith(R.id.btLogin) {
                viewModel.registerUser(
                    binding.editemail.text.toString(),
                    binding.editpass.text.toString(),
                    binding.editUsername.text.toString()
                )
            }
        }


        binding.Fblogin.setOnClickListener {
            viewModel.loginManager.logInWithReadPermissions(this,facebook_permissions)
        }


        binding.Glogin.setOnClickListener {
            viewModel.googleSignIn().also {
                startActivityForResult(it, R_CODE_GSIGN_IN)
            }

        }
    }
    private fun cekLogin() {
        val user = auth.currentUser
        if (user != null) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)

        } else {

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleOnActivityResult(requestCode, resultCode, data)

    }
}