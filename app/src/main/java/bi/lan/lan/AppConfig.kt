package bi.lan.lan

object AppConfig {
    // Change these to your actual backend IPs
    const val CUSTOMER_BASE_URL = "http://192.168.1.218:8010"
    const val AGENT_BASE_URL = "http://192.168.1.218:8002"

    // Blink API
    const val BLINK_API_URL = "https://api.blink.sv/graphql"
    val BLINK_ACCESS_TOKEN = BuildConfig.BLINK_ACCESS_TOKEN
}
