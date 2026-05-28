package bi.lan.lan.di

import bi.lan.lan.AppConfig
import bi.lan.lan.data.agent.api.AgentLightningApi
import bi.lan.lan.data.customer.api.CustomerLightningApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun createHttpClient(baseUrl: String): HttpClient {
    return HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("Ktor: $message")
                }
            }
            level = LogLevel.BODY
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            url(baseUrl)
        }
    }
}

val networkModule = module {
    single(named("customerClient")) { createHttpClient(AppConfig.CUSTOMER_BASE_URL) }
    single(named("agentClient")) { createHttpClient(AppConfig.AGENT_BASE_URL) }
    single { CustomerLightningApi(get(named("customerClient"))) }
    single { AgentLightningApi(get(named("agentClient"))) }
}
