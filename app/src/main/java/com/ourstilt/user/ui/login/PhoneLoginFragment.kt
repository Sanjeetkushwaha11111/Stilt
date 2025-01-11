package com.ourstilt.user.ui.login

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ourstilt.R
import com.ourstilt.databinding.FragmentPhoneLoginBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PhoneLoginFragment : DialogFragment() {

    private lateinit var binding: FragmentPhoneLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginButton.apply {
            setOnClickListener {
                val spinner = binding.loaderImage
                spinner.showWithPopupEffect()
                spinner.setColor(Color.parseColor("#FFFFFF"))
            }
            lifecycleScope.launch {
                delay(10000)
                binding.loaderImage.apply {
                    hideWithPopInEffect()
                }
                findNavController().navigate(R.id.action_loginFragment_to_otpFragment)
            }
        }

    }
}
