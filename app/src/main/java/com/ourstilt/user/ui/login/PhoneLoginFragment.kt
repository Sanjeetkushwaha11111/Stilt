package com.ourstilt.user.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ourstilt.customViews.SpinningLoader
import com.ourstilt.databinding.FragmentPhoneLoginBinding


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
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginButton.setOnClickListener {
            showLoader(true, loaderView)
        }
        binding.root.setOnClickListener {
            showLoader(false, loaderView)
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
