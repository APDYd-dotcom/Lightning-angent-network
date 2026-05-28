package bi.lan.lan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import bi.lan.lan.presentation.navigation.AppNavigation
import bi.lan.lan.ui.theme.LANTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LANTheme {
                AppNavigation()
            }
        }
    }
}