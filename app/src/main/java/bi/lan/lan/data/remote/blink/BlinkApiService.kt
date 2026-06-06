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
        val query = "query { me { defaultAccount { wallets { id balance walletCurrency } } } }"
        return executeQuery(query)
    }

    suspend fun createInvoice(amount: Long, memo: String, walletId: String): BlinkInvoiceResponse {
        val query = """
            mutation Mutation(${'$'}input: LnInvoiceCreateOnBehalfOfRecipientInput!) {
                lnInvoiceCreateOnBehalfOfRecipient(input: ${'$'}input) {
                    invoice {
                        paymentRequest
                        paymentHash
                        satoshis
                    }
                    errors { message }
                }
            }
        """.trimIndent()

        val variables = buildJsonObject {
            put("input", buildJsonObject {
                put("recipientWalletId", walletId)
                put("amount", amount.toString())
                put("memo", memo)
                put("expiresIn", 15)
            })
        }

        return executeQuery(query, variables)
    }

    suspend fun payInvoice(paymentRequest: String, walletId: String): BlinkPaymentResponse {
        val query = """
            mutation Mutation(${'$'}input: LnInvoicePaymentInput!) {
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

        return executeQuery(query, variables)
    }

    suspend fun payNoAmountInvoice(paymentRequest: String, amount: Long, walletId: String): BlinkPaymentResponse {
        val query = """
            mutation Mutation(${'$'}input: LnNoAmountInvoicePaymentInput!) {
                lnNoAmountInvoicePaymentSend(input: ${'$'}input) {
                    status
                    errors { message }
                }
            }
        """.trimIndent()

        val variables = buildJsonObject {
            put("input", buildJsonObject {
                put("paymentRequest", paymentRequest)
                put("amount", amount)
                put("walletId", walletId)
            })
        }

        return executeQuery(query, variables)
    }

    suspend fun getTransactions(limit: Int = 20): BlinkTransactionsResponse {
        val query = """
            query transactions(${'$'}first: Int) {
              me {
                defaultAccount {
                  transactions(first: ${'$'}first) {
                    edges {
                      node {
                        id
                        initiationVia {
                          __typename
                          ... on InitiationViaLn {
                            paymentRequest
                          }
                        }
                        settlementVia {
                          __typename
                          ... on SettlementViaIntraLedger {
                            counterpartyWalletId
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

        return executeQuery(query, variables)
    }

    suspend fun decodeInvoice(paymentRequest: String): BlinkDecodeInvoiceResponse {
        val query = """
            query lnInvoiceDecode(${'$'}input: LnInvoiceDecodeInput!) {
                lnInvoiceDecode(input: ${'$'}input) {
                    paymentHash
                    satoshis
                    memo
                    expiry
                }
            }
        """.trimIndent()

        val variables = buildJsonObject {
            put("input", buildJsonObject {
                put("paymentRequest", paymentRequest)
            })
        }

        return executeQuery(query, variables)
    }

    private suspend inline fun <reified T> executeQuery(query: String, variables: JsonObject? = null): T {
        val requestBody = GraphQLRequest(query = query, variables = variables)
        println("BLINK REQUEST: $requestBody")
        
        val response = client.post(apiUrl) {
            header("X-API-KEY", accessToken)
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }
        
        val responseBody = response.body<String>()
        println("BLINK RESPONSE: $responseBody")

        if (response.status != HttpStatusCode.OK) {
            throw Exception("Blink API HTTP error ${response.status.value}: $responseBody")
        }
        
        val result = json.decodeFromString<JsonObject>(responseBody)
        if (result.containsKey("errors")) {
            val errors = result["errors"]?.jsonArray
            if (errors != null && errors.isNotEmpty()) {
                val firstError = errors[0].jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown GraphQL error"
                throw Exception("Blink GraphQL error: $firstError")
            }
        }

        return json.decodeFromString(responseBody)
    }
    
    private val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun getAccountDetails(): BlinkAccountDetailsResponse {
        val query = """
            query GetAccountDetails {
              me {
                id
                username
                createdAt
                defaultAccount {
                  wallets {
                    id
                    balance
                    walletCurrency
                  }
                }
              }
            }
        """.trimIndent()

        return executeQuery(query)
    }

    suspend fun checkInvoiceStatus(paymentRequest: String): BlinkInvoiceStatusResponse {
        val query = """
            query CheckInvoiceStatus(${'$'}input: LnInvoicePaymentStatusInput!) {
                lnInvoicePaymentStatus(input: ${'$'}input) {
                    status
                }
            }
        """.trimIndent()

        val variables = buildJsonObject {
            put("input", buildJsonObject {
                put("paymentRequest", paymentRequest)
            })
        }

        return executeQuery(query, variables)
    }
}
