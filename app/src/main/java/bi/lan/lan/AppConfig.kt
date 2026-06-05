package bi.lan.lan

object AppConfig {
    // Change these to your actual backend IPs
    const val CUSTOMER_BASE_URL = "http://192.168.1.218:8010"
    const val AGENT_BASE_URL = "http://192.168.1.218:8002"

    // Blink API
    const val BLINK_API_URL = "https://api.blink.sv/graphql"
    const val BLINK_ACCESS_TOKEN = "blink_dV5yY3K2ds3YI5ZwjxqFueXhx0dZ6Md4kUbHFx1ZB3iHmV5HYfL0noit0tn25TuM"

    // LNbits API
    const val LNBITS_BASE_URL = "https://legend.lnbits.com"
    const val LNBITS_ADMIN_KEY = "your_lnbits_admin_key" // To be provided by user
    const val LNBITS_INVOICE_KEY = "your_lnbits_invoice_key"
}

