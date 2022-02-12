package com.asterisk.grocerylist.ui.auth

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.asterisk.grocerylist.R
import com.asterisk.grocerylist.databinding.FragmentAuthBinding
import kotlin.concurrent.fixedRateTimer


class AuthFragment : Fragment(R.layout.fragment_auth) {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private var cancellationSignal: CancellationSignal? = null

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() = @RequiresApi(Build.VERSION_CODES.P)
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                notifyUser("Authentication error $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                val action = AuthFragmentDirections.actionAuthFragmentToGroceryListFragment()
                findNavController().navigate(action)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                notifyUser("Authentication failed")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkBiometricSupport()

        binding.btnLogin.setOnClickListener {
            val biometricPrompt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                BiometricPrompt.Builder(requireContext())
                    .setTitle("Login Authentication")
                    .setSubtitle("Authentication is required")
                    .setDescription("This app uses fingerprint protection to keep your data secured.")
                    .setNegativeButton(
                        "Cancel",
                        activity?.mainExecutor!!,
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            notifyUser("Authentication cancelled")
                        }).build()
            } else {
                TODO("VERSION.SDK_INT < P")
            }

            biometricPrompt.authenticate(getCancellationSignal(),
                activity?.mainExecutor!!, authenticationCallback)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkBiometricSupport(): Boolean {
        val keyGuardManager =
            activity?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyGuardManager.isKeyguardSecure) {
            notifyUser("Fingerprint authentication has not been enabled")
            return false
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notifyUser("Fingerprint permission is not enable")
            return false
        }

        return activity?.baseContext?.packageManager?.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) == true

    }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was cancelled by user")
        }
        return cancellationSignal as CancellationSignal
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun notifyUser(string: String) {
        Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
    }

}