package bi.lan.lan.domain.repository

import bi.lan.lan.data.model.User
import bi.lan.lan.data.remote.NetworkResult

interface AuthRepository {
    suspend fun login(phoneNumber: String): NetworkResult<String>
    suspend fun verifyOtp(phoneNumber: String, otp: String): NetworkResult<User>
    suspend fun isLoggedIn(): Boolean
    suspend fun logout()
}
