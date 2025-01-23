package com.ourstilt.userlogin.ui

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ourstilt.common.slideDown
import com.ourstilt.common.startWithSlideUp
import com.ourstilt.customViews.SpinningLoader
import com.ourstilt.databinding.FragmentOtpBinding
import com.ourstilt.homepage.ui.HomeActivity

class OtpFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentOtpBinding
    private val viewModel: LandingViewModel by activityViewModels()
    private val expectedOtp = "12345"
    private val loaderView by lazy {
        val viewStub = binding.loaderStub
        viewStub.inflate() as SpinningLoader
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)
        setupOtpListner()
        //fetchPhoneOtp()
        binding.backButton.apply {
            setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun fetchPhoneOtp() {
        val otp = arguments?.getString("otp")
        if (otp != null) {
            binding.otpView.setText((otp))
            verifyOtp(otp)
        }
    }

    private fun setupOtpListner() {
        binding.otpView.apply {
            this.setOnFinishListener { it ->
                if (it.length == 6) {
                    verifyOtp(it)
                } else {
                    binding.statusText.apply {
                        text = "Please enter valid otp"
                        slideDown(700)
                    }
                    binding.otpView.clearText(true)
                }
            }
        }
    }

    private fun verifyOtp(otp: String) {
        viewModel.verifyOtp(otp)
        viewModel.otpValidation.observe(viewLifecycleOwner) {
            when (it) {
                is LandingViewModel.OtpValidationState.Checking -> {
                    showLoader(true, loaderView)
                }

                is LandingViewModel.OtpValidationState.Failed -> {
                    showLoader(false, loaderView)
                    binding.statusText.apply {
                        text = "Wrong otp, Retry!"
                        slideDown(400)
                    }
                }

                is LandingViewModel.OtpValidationState.Success -> {
                    binding.statusText.apply {
                        text = "Otp Verified"
                        slideDown(400)
                    }
                    showLoader(false, loaderView)
                    viewModel.userLogInSuccess.value = true
                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    requireActivity().startWithSlideUp(intent)
                    requireActivity().finish()
                }
            }
        }
    }

    private fun showLoader(show: Boolean, loaderView: SpinningLoader) {
        loaderView.apply {
            if (show) {
                showWithPopupEffect()
            } else {
                hideWithPopInEffect()
            }
        }
    }
}
