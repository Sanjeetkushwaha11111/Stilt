package com.ourstilt.userlogin.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ourstilt.common.isPhoneNumber
import com.ourstilt.customViews.SpinningLoader
import com.ourstilt.databinding.FragmentPhoneLoginBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


class PhoneLoginFragment : Fragment() {

    private lateinit var binding: FragmentPhoneLoginBinding

    private val loaderView by lazy {
        val viewStub = binding.loaderStub
        viewStub.inflate() as SpinningLoader
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneLoginBinding.inflate(inflater, container, false)
        return binding.mainCl
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            delay(1000)
        }

        binding.phoneEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val phoneNumber = s.toString()
                if (phoneNumber.length >= 10) {
                    if (phoneNumber.isPhoneNumber()) {
                        sendOtp()
                    } else {
                        Timber.e(">>>>>>>>>>> invalid phone")
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    private fun sendOtp() {
        binding.loginButton.apply {
            isEnabled = true
            isClickable=false
        }
        showLoader(true,loaderView)
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
