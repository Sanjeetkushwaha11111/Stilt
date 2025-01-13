package com.ourstilt.userlogin.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ourstilt.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {


    var userLogInSuccess = MutableLiveData<Boolean>()

    private val _phoneValidation = MutableLiveData<PhoneValidationState>()
    val phoneValidation: LiveData<PhoneValidationState> = _phoneValidation


    private val _otpValidation = MutableLiveData<OtpValidationState>()
    val otpValidation: LiveData<OtpValidationState> = _otpValidation


    fun validatePhoneNumber(inputNumber: String) {
        viewModelScope.launch {
            val cleanNumber = formatPhoneNumber(inputNumber)
            when {
                cleanNumber.isEmpty() -> {
                    _phoneValidation.value = PhoneValidationState.Empty
                }

                cleanNumber.length != 10 -> {
                    _phoneValidation.value = PhoneValidationState.InvalidLength
                }

                !cleanNumber.isValidIndianMobile() -> {
                    _phoneValidation.value = PhoneValidationState.InvalidFormat
                }

                else -> {
                    _phoneValidation.value = PhoneValidationState.Valid(cleanNumber)
                }
            }
        }
    }

    fun verifyOtp(otp: String) {
        viewModelScope.launch {
            _otpValidation.value = OtpValidationState.Checking
            delay(3000)
            if (otp == "123456") {
                _otpValidation.value = OtpValidationState.Success
            } else {
                _otpValidation.value = OtpValidationState.Failed
            }
        }
    }


    private fun formatPhoneNumber(input: String): String {
        var number = input.replace(Regex("[^0-9]"), "")
        if (number.startsWith("91") && number.length > 2) {
            number = number.substring(2)
        }
        return number
    }

    private fun String.isValidIndianMobile(): Boolean {
        return Pattern.compile("^[6-9]\\d{9}$").matcher(this).matches()
    }

    fun resetPhoneValidation() {
        _phoneValidation.value = PhoneValidationState.Empty
    }


    sealed class PhoneValidationState {
        data object Empty : PhoneValidationState()
        data object InvalidLength : PhoneValidationState()
        data object InvalidFormat : PhoneValidationState()
        data class Valid(val number: String) : PhoneValidationState()
    }

    sealed class OtpValidationState {
        data object Checking : OtpValidationState()
        data object Failed : OtpValidationState()
        data object Success : OtpValidationState()
    }
}