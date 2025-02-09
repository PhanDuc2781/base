package com.example.base_project

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.viewModelScope
import com.example.base_project.base.BaseActivity
import com.example.base_project.base.BaseVMActivity
import com.example.base_project.base.BaseViewModel
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.data.remote.repository.RequestOTPRepo
import com.example.base_project.data.remote.request.OTPRequest
import com.example.base_project.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity :
    BaseVMActivity<ActivityMainBinding, MainViewModel>(ActivityMainBinding::inflate) {

    override val viewModel: MainViewModel by viewModels()
    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        binding.send.setOnSingleClickListener {
            viewModel.requestOTP(
                OTPRequest(
                    email = binding.editText.text.toString(),
                    type = "SIGN_IN",
                    workspace_id = 0
                )
            )
        }
    }
}

@HiltViewModel
class MainViewModel @Inject constructor(private val requestOTPRepo: RequestOTPRepo) :
    BaseViewModel() {

    fun requestOTP(otpRequest: OTPRequest) = viewModelScope.launch {
        requestOTPRepo.requestOtp(otpRequest).onSuccess {
            Log.d("RESULT", " $it")
        }.onFailure {

        }
    }
}