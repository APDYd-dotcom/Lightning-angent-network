package bi.lan.lan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.Color
import bi.lan.lan.presentation.navigation.AppNavigation
import bi.lan.lan.ui.theme.LANTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.navigationBarColor = Color.Transparent
        setContent {
            LANTheme {
                AppNavigation()
            }
        }
    }
}