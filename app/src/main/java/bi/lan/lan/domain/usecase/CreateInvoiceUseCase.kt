package bi.lan.lan.domain.usecase

import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.LightningRepository

class CreateInvoiceUseCase(private val repository: LightningRepository) {
    suspend operator fun invoke(amount: Long, memo: String): NetworkResult<String> {
        return when (val result = repository.createInvoice(amount, memo, 3600)) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.paymentRequest)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}
