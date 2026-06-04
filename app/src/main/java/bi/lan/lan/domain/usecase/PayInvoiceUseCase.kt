package bi.lan.lan.domain.usecase

import bi.lan.lan.data.remote.NetworkResult
import bi.lan.lan.domain.repository.LightningRepository

class PayInvoiceUseCase(private val repository: LightningRepository) {
    suspend operator fun invoke(paymentRequest: String): NetworkResult<String> {
        return when (val result = repository.payInvoice(paymentRequest, null)) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.paymentHash)
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}
