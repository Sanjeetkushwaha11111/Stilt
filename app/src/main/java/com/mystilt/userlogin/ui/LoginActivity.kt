package com.mystilt.userlogin.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.mystilt.databinding.ActivityLoginBinding
import com.mystilt.deeplink.DeepLinkResponse
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context, deepLinkResponse: DeepLinkResponse?=null): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
    private lateinit var binding: ActivityLoginBinding

    private val landingViewModel: LandingViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        window.decorView.clipToOutline = true
        setContentView(binding.root)
        getFcmToken()

    }

    private fun getFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result
            if (token != null) landingViewModel.fcmToken(token)
            Timber.e(">>>>>>>>>>GET FCM Token: $token")
        }.addOnFailureListener {
            Timber.e(">>>>>>>>>>GET_FCM_FAILED, it.toString() ")
        }
    }
}

