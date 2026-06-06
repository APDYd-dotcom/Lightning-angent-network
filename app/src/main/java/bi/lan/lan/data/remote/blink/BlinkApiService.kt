package bi.lan.lan.data.remote.blink

import bi.lan.lan.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.*

class BlinkApiService(private val client: HttpClient) {

    private val apiUrl = AppConfig.BLINK_API_URL
    private val accessToken = AppConfig.BLINK_ACCESS_TOKEN

    suspend fun getBalance(): BlinkBalanceResponse {
        val response = client.post(apiUrl) {
            header("X-API-KEY", accessToken)
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = "query { me { defaultAccount { wallets { id balance walletCurrency } } } }"))
        }
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Blink API error: ${response.status.value} ${response.status.description}")
        }
        return response.body()
    }

    suspend fun createInvoice(amount: Long, memo: String, walletId: String): BlinkInvoiceResponse {
        val query = """
            mutation lnInvoiceCreate(${'$'}input: LnInvoiceCreateInput!) {
              lnInvoiceCreate(input: ${'$'}input) {
                errors { message }
                invoice { paymentRequest paymentHash }
              }
            }
        """.trimIndent()

        val variables = buildJsonObject {
            put("input", buildJsonObject {
                put("amount", amount)
                put("memo", memo)
                put("walletId", walletId)
            })
        }

        val response = client.post(apiUrl) {
            header("X-API-KEY", accessToken)
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = query, variables = variables))
        }
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Blink API error: ${response.status.value} ${response.status.description}")
        }
        return response.body()
    }

    suspend fun payInvoice(paymentRequest: String, walletId: String): BlinkPaymentResponse {
        val query = """
            mutation lnInvoicePaymentSend(${'$'}input: LnInvoicePaymentSendInput!) {
              lnInvoicePaymentSend(input: ${'$'}input) {
                status
                errors { message }
              }
            }
        """.trimIndent()

        val variables = buildJsonObject {
            put("input", buildJsonObject {
                put("paymentRequest", paymentRequest)
                put("walletId", walletId)
            })
        }

        val response = client.post(apiUrl) {
            header("X-API-KEY", accessToken)
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = query, variables = variables))
        }
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Blink API error: ${response.status.value} ${response.status.description}")
        }
        return response.body()
    }

    suspend fun getTransactions(limit: Int = 20): BlinkTransactionsResponse {
        val query = """
            query getTransactions(${'$'}first: Int) {
              me {
                defaultAccount {
                  transactions(first: ${'$'}first) {
                    edges {
                      node {
                        id
                        initiationVia {
                          ... on InitiationViaLn {
                            paymentRequest
                            type
                          }
                          ... on InitiationViaIntraLedger {
                            type
                          }
                          ... on InitiationViaOnChain {
                            type
                          }
                        }
                        settlementVia {
                          ... on SettlementViaLn {
                            type
                          }
                          ... on SettlementViaIntraLedger {
                            counterpartyWalletId
                            type
                          }
                          ... on SettlementViaOnChain {
                            type
                          }
                        }
                        settlementAmount
                        settlementCurrency
                        settlementDisplayAmount
                        settlementDisplayCurrency
                        settlementDisplayFee
                        status
                        memo
                        createdAt
                      }
                    }
                  }
                }
              }
            }
        """.trimIndent()

        val variables = buildJsonObject {
            put("first", limit)
        }

        val response = client.post(apiUrl) {
            header("X-API-KEY", accessToken)
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = query, variables = variables))
        }
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Blink API error: ${response.status.value} ${response.status.description}")
        }
        return response.body()
    }
}
