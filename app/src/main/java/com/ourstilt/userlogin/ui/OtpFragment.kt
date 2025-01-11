package com.ourstilt.userlogin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ourstilt.databinding.FragmentOtpBinding

class OtpFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentOtpBinding
    private val expectedOtp = "12345"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.apply {
            setOnClickListener {
                findNavController().navigateUp()
            }
        }
        initOTPView()
    }

    private fun initOTPView() {
        binding.otpView.apply {
            setOnFinishListener { otp ->
                Toast.makeText(requireContext(), otp, Toast.LENGTH_LONG).show()
            }

            setOnCharacterUpdatedListener { isFilled ->

            }
        }
    }
}
