package com.ourstilt.userlogin.ui

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ourstilt.R
import com.ourstilt.common.hideKeyboard
import com.ourstilt.common.showKeyboard
import com.ourstilt.common.showToastShort
import com.ourstilt.customViews.SpinningLoader
import com.ourstilt.databinding.FragmentPhoneLoginBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PhoneLoginFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentPhoneLoginBinding
    private val viewModel: LandingViewModel by activityViewModels()
    private var autoProceed = true
    private var loaderView: SpinningLoader? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPhoneValidation()
        viewModelObserver()
        loaderView = binding.loaderStub.inflate() as SpinningLoader
        lifecycleScope.launch {
            delay(1000)
            binding.phoneEt.requestFocus()
            binding.phoneEt.showKeyboard()
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)
        }
    }

    private fun viewModelObserver() {
        viewModel.phoneValidation.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LandingViewModel.PhoneValidationState.Empty -> {
                    binding.loginButton.isEnabled = true
                    binding.loginButton.isClickable = true
                }

                is LandingViewModel.PhoneValidationState.InvalidLength -> {
                    context?.showToastShort("Phone number must be 10 digits")
                    binding.loginButton.isEnabled = true
                    binding.loginButton.isClickable = true
                    autoProceed = true
                }

                is LandingViewModel.PhoneValidationState.InvalidFormat -> {
                    context?.showToastShort("Invalid phone number format")
                    binding.loginButton.isEnabled = true
                    binding.loginButton.isClickable = true
                    autoProceed = true
                }

                is LandingViewModel.PhoneValidationState.Valid -> {
                    sendOtp(state.number)
                }
            }
        }
    }

    private fun setupPhoneValidation() {
        binding.phoneEt.filters =
            arrayOf(InputFilter.LengthFilter(10), InputFilter { source, _, _, _, _, _ ->
                source.toString().replace(Regex("[^0-9]"), "")
            })
        binding.phoneEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(phoneNum: Editable) {
                if (autoProceed) {
                    if (phoneNum.length >= 10) {
                        phoneNum.toString().let { viewModel.validatePhoneNumber(it) }
                        autoProceed = false
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.loginButton.setOnClickListener {
            binding.phoneEt.text.toString().let { phoneNumber ->
                phoneNumber.let {
                    viewModel.validatePhoneNumber(it)
                }
            }
        }
    }

    private fun sendOtp(phoneNumber: String) {
        showLoader(true)
        viewModel.resetPhoneValidation()
        binding.loginButton.apply {
            isEnabled = true
            isClickable = false
        }
        binding.phoneEt.apply {
            binding.phoneEt.clearFocus()
            binding.phoneEt.hideKeyboard()
        }
        lifecycleScope.launch {
            delay(3000)
            context?.showToastShort("OTP Sent")
            showLoader(false)
            delay(1000)
            findNavController().navigate(R.id.action_loginFragment_to_otpFragment)
        }
    }

    private fun showLoader(show: Boolean) {
        loaderView?.apply {
            if (show) {
                showWithPopupEffect()
            } else {
                hideWithPopInEffect()
            }
        }
    }
}


