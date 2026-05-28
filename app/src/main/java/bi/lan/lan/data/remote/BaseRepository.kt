package bi.lan.lan.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseRepository {
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                NetworkResult.Success(response)
            } catch (e: Exception) {
                // In Ktor, exceptions like ResponseException contain status codes.
                // We're simplifying error handling for the MVP here.
                e.printStackTrace()
                NetworkResult.Exception(e)
            }
        }
    }
}
