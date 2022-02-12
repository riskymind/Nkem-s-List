package com.asterisk.grocerylist.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.asterisk.grocerylist.R
import com.asterisk.grocerylist.databinding.FragmentSplashBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SplashFragment : Fragment(R.layout.fragment_splash), CoroutineScope {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSplashBinding.bind(view)

        binding.apply {

        }

        launch {
            delay(2000)
//            val action = SplashFragmentDirections.actionSplashFragmentToAuthFragment()
            val action = SplashFragmentDirections.actionSplashFragmentToGroceryListFragment()
            findNavController().navigate(action)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

}