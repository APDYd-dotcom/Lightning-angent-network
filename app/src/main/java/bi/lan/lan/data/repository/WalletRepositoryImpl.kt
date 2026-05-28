package bi.lan.lan.data.repository

import bi.lan.lan.data.api.ApiClient
import bi.lan.lan.data.api.LightningApiService
import bi.lan.lan.data.model.Wallet
import bi.lan.lan.data.remote.BaseRepository
import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.WalletRepository
import kotlinx.coroutines.delay

class WalletRepositoryImpl(
    private val apiService: LightningApiService
) : BaseRepository(), WalletRepository {

    override suspend fun getWallet(): NetworkResult<Wallet> {
        return if (ApiClient.IS_MOCK_MODE) {
            delay(1000)
            NetworkResult.Success(Wallet(balanceSats = 150000, address = "lnbc1..."))
        } else {
            safeApiCall {
                val res = apiService.getWallet()
                res.data ?: throw Exception(res.message ?: "Failed to get wallet")
            }
        }
    }
}
