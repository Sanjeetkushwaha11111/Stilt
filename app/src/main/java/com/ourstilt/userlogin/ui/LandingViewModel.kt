package com.ourstilt.userlogin.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ourstilt.base.data.api.NetworkResult
import com.ourstilt.base.data.repository.DataStoreRepository
import com.ourstilt.base.ui.BaseViewModel
import com.ourstilt.deeplink.DeepLinkResponse
import com.ourstilt.userlogin.data.LandingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.regex.Pattern
import javax.inject.Inject


@HiltViewModel
class LandingViewModel @Inject constructor(
    private val dataStoreRepo: DataStoreRepository, private val repository: LandingRepository
) : BaseViewModel() {

    var userLogInSuccess = MutableLiveData<Boolean>()

    val isUserLoggedIn: Flow<Boolean> = dataStoreRepo.isUserLoggedIn

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
                dataStoreRepo.setUserLoggedIn(true)
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

    private val _navigationEvent = MutableStateFlow<SplashNavigation?>(null)
    val navigationEvent: StateFlow<SplashNavigation?> = _navigationEvent


    val deepLinkResponse = DeepLinkResponse(
        targetUrl = "someUrl",
        type = "home",
        data = listOf(mapOf("key1" to "value1", "key2" to "value2"))
    )

    fun processDeepLink(deepLink: Uri?) {
        viewModelScope.launch {
            if (repository.isUserLoggedIn()) {
                if (deepLink != null) {
                    repository.getDeepLinkResponse(deepLink).onStart { _loading.value = true }
                        .onCompletion { _loading.value = false }.collect { result ->
                            when (result) {
                                is NetworkResult.Success -> {
                                    Timber.d("Deep link response received: ${result.data}")
                                    _navigationEvent.value =
                                        SplashNavigation.NavigateToTarget(result.data)
                                }

                                is NetworkResult.Error -> {
                                    Timber.e("Error fetching deep link data: ${result.message}")
                                    if (repository.isUserLoggedIn()) {
                                        _navigationEvent.value = SplashNavigation.NavigateToHome
                                    } else {
                                        _navigationEvent.value = SplashNavigation.NavigateToLogin
                                    }
                                }

                                is NetworkResult.Loading -> {
                                    Timber.d("Fetching deep link data...")
                                }
                            }
                        }
                } else {
                    _navigationEvent.value = SplashNavigation.NavigateToHome
                }
            } else {
                _navigationEvent.value = SplashNavigation.NavigateToLogin
            }
        }
    }


    sealed class SplashNavigation {
        data class NavigateToTarget(val deepLinkResponse: DeepLinkResponse) : SplashNavigation()
        data object NavigateToLogin : SplashNavigation()
        data object NavigateToHome : SplashNavigation()
    }

}