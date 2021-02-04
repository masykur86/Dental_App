package com.julyapp.dentalapp.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth

class DentalViewModelProviderFactory(val app:Application,val auth:FirebaseAuth,val context: Context):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DentalViewModel(app,auth, context) as T
    }

}