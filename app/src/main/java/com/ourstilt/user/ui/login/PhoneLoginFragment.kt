package com.ourstilt.user.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.ourstilt.R
import com.ourstilt.base.BaseFragment
import com.ourstilt.databinding.FragmentPhoneLoginBinding

class PhoneLoginFragment : DialogFragment() {

    private lateinit var binding: FragmentPhoneLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.RoundedFragmentStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_otpFragment)
        }
    }
}
