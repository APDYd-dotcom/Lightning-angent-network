package bi.lan.lan.data.repository

import bi.lan.lan.data.api.ApiClient
import bi.lan.lan.data.api.LightningApiService
import bi.lan.lan.data.model.User
import bi.lan.lan.data.remote.BaseRepository
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.AuthRepository
import kotlinx.coroutines.delay

class AuthRepositoryImpl(
    private val apiService: LightningApiService
) : BaseRepository(), AuthRepository {
    
    private var isUserLoggedIn = false

    override suspend fun login(phoneNumber: String): NetworkResult<String> {
        return if (ApiClient.IS_MOCK_MODE) {
            delay(1000)
            NetworkResult.Success("OTP sent to $phoneNumber")
        } else {
            safeApiCall {
                apiService.login(phoneNumber).message ?: "Success"
            }
        }
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): NetworkResult<User> {
        return if (ApiClient.IS_MOCK_MODE) {
            delay(1000)
            if (otp == "1234") {
                isUserLoggedIn = true
                NetworkResult.Success(User("1", phoneNumber, "Test User"))
            } else {
                NetworkResult.Error(400, "Invalid OTP")
            }
        } else {
            safeApiCall {
                val res = apiService.verifyOtp(phoneNumber, otp)
                if (res.success && res.data != null) {
                    isUserLoggedIn = true
                    res.data
                } else {
                    throw Exception(res.message ?: "Verification failed")
                }
            }
        }
    }

    override suspend fun isLoggedIn(): Boolean = isUserLoggedIn

    override suspend fun logout() {
        isUserLoggedIn = false
    }
}
