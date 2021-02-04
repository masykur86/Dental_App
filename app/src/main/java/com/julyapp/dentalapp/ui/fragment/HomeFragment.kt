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
import com.julyapp.dentalapp.databinding.FragmentHomeBinding
import com.julyapp.dentalapp.databinding.FragmentLoginBinding
import com.julyapp.dentalapp.databinding.FragmentRegisterBinding
import com.julyapp.dentalapp.ui.DentalViewModel
import com.julyapp.dentalapp.ui.MainActivity


class HomeFragment : Fragment(R.layout.fragment_home) {
    lateinit var viewModel: DentalViewModel
    lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding

    companion object {
        private val facebook_permissions = mutableListOf("email", "public_profile")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        binding = FragmentHomeBinding.bind(view)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        viewModel.currentUser.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        })
        binding.textView.text = user?.email.toString()
        binding.logout.setOnClickListener {
            Firebase.auth.signOut()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

    }


}