package bi.lan.lan.domain.repository

import bi.lan.lan.data.model.Wallet
import bi.lan.lan.data.remote.NetworkResult

interface WalletRepository {
    suspend fun getWallet(): NetworkResult<Wallet>
}
