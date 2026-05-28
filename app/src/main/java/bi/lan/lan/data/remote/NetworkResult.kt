package bi.lan.lan.data.remote

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int? = null, val message: String) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}
