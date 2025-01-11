package com.ourstilt.userlogin.ui

import com.ourstilt.base.BaseViewModel
import com.ourstilt.userlogin.data.UserRepo
import javax.inject.Inject


class LoginViewModel @Inject constructor(private val userRepository: UserRepo) :
    BaseViewModel() {


}