package bi.lan.lan.data.api

import bi.lan.lan.data.model.Agent
import bi.lan.lan.data.model.ApiResponse
import bi.lan.lan.data.model.DepositRequest
import bi.lan.lan.data.model.PaymentInvoice
import bi.lan.lan.data.model.Transaction
import bi.lan.lan.data.model.User
import bi.lan.lan.data.model.Wallet
import bi.lan.lan.data.model.WithdrawRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.Serializable

class LightningApiService(private val client: HttpClient) {

    @Serializable
    data class LoginReq(val phoneNumber: String)
    @Serializable
    data class OtpReq(val phoneNumber: String, val otp: String)

    suspend fun login(phoneNumber: String): ApiResponse<String> {
        return client.post("auth/login/") {
            setBody(LoginReq(phoneNumber))
        }.body()
    }

    suspend fun verifyOtp(phoneNumber: String, otp: String): ApiResponse<User> {
        return client.post("auth/verify-otp/") {
            setBody(OtpReq(phoneNumber, otp))
        }.body()
    }

    suspend fun getWallet(): ApiResponse<Wallet> {
        return client.get("wallet/me/").body()
    }

    suspend fun getNearbyAgents(): ApiResponse<List<Agent>> {
        return client.get("agents/nearby/").body()
    }

    suspend fun createDeposit(request: DepositRequest): ApiResponse<PaymentInvoice> {
        return client.post("deposits/create/") {
            setBody(request)
        }.body()
    }

    suspend fun getDepositStatus(id: String): ApiResponse<Transaction> {
        return client.get("deposits/$id/status/").body()
    }

    suspend fun requestWithdrawal(request: WithdrawRequest): ApiResponse<Transaction> {
        return client.post("withdrawals/request/") {
            setBody(request)
        }.body()
    }

    suspend fun getTransactions(): ApiResponse<List<Transaction>> {
        return client.get("transactions/").body()
    }

    suspend fun getAgentDashboard(): ApiResponse<Agent> {
        return client.get("agent/dashboard/").body()
    }
}
