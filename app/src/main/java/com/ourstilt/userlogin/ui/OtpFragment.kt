package com.ourstilt.userlogin.ui

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ourstilt.R
import com.ourstilt.customViews.SpinningLoader
import com.ourstilt.databinding.FragmentOtpBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OtpFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentOtpBinding
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
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding.help.setOnClickListener {
            fillOtp("123456")
        }

        binding.backButton.apply {
            setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun fillOtp(otp: String) {
        binding.otpView.setText((otp))
        binding.loginButton.apply {
            setOnClickListener {
                showLoader(true, loaderView)
                isEnabled = true
                isClickable = false
                lifecycleScope.launch {
                    delay(3000)
                    showLoader(false, loaderView)
                    findNavController().navigate(R.id.action_otpFragment_to_registerFragment)
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
