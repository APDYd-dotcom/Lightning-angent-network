package bi.lan.lan.domain.usecase

import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.LightningRepository

class GetBalanceUseCase(private val repository: LightningRepository) {
    suspend operator fun invoke(): NetworkResult<Long> {
        return when (val result = repository.getBalance()) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.walletBalance.totalBalance)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}
