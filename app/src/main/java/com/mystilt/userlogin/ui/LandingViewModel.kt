package com.mystilt.userlogin.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mystilt.base.data.api.NetworkResult
import com.mystilt.base.data.repository.DataStoreRepository
import com.mystilt.base.ui.BaseViewModel
import com.mystilt.deeplink.DeepLinkResponse
import com.mystilt.deeplink.Event
import com.mystilt.userlogin.data.LandingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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

    private val _navigationEvent = MutableLiveData<Event<SplashNavigation?>>()
    val navigationEvent: LiveData<Event<SplashNavigation?>> = _navigationEvent

    fun processDeepLink(deepLink: Uri?) {
        viewModelScope.launch {
            if (repository.isUserLoggedIn()) {
                if (deepLink != null) {
                    repository.getDeepLinkResponse(true, deepLink).onStart { _loading.value = true }
                        .onCompletion { _loading.value = false }.collect { result ->
                            when (result) {
                                is NetworkResult.Success -> {
                                    _navigationEvent.postValue(Event(SplashNavigation.NavigateToTarget(result.data)))
                                }
                                is NetworkResult.Error -> {
                                    if (repository.isUserLoggedIn()) {
                                        _navigationEvent.postValue(Event(SplashNavigation.NavigateToHome))
                                    } else {
                                        _navigationEvent.postValue(Event(SplashNavigation.NavigateToLogin))
                                    }
                                }
                                is NetworkResult.Loading -> {
                                    Timber.e(">>>>>>>>> Fetching deep link data...")
                                }
                            }
                        }
                } else {
                    _navigationEvent.postValue(Event(SplashNavigation.NavigateToHome))
                }
            } else {
                _navigationEvent.postValue(Event(SplashNavigation.NavigateToLogin))
            }
        }
    }

    fun fcmToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepo.fcmToken(token)
        }
    }

    sealed class SplashNavigation {
        data class NavigateToTarget(val deepLinkResponse: DeepLinkResponse) : SplashNavigation()
        data object NavigateToLogin : SplashNavigation()
        data object NavigateToHome : SplashNavigation()
    }
}