package bi.lan.lan

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import bi.lan.lan.presentation.navigation.AppNavigation
import bi.lan.lan.ui.theme.LANTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Custom exit animation for a premium "pro" feel
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val root = splashScreenView.view
            val icon = splashScreenView.iconView

            // Create App Name TextView programmatically to use strings.xml
            val appNameView = TextView(this).apply {
                text = getString(R.string.app_name).uppercase()
                setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                textSize = 32f
                typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
                gravity = Gravity.CENTER
                alpha = 0f
                translationY = 150f
                letterSpacing = 0.2f
            }

            if (root is FrameLayout) {
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                    topMargin = 300 // Positioned nicely below the center icon
                }
                root.addView(appNameView, params)
            }

            // Animation sequence: 
            // 1. Icon scales up slightly with overshoot then fades out
            // 2. Text slides up and fades in with overshoot
            
            val iconScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.1f, 0.4f)
            val iconScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.1f, 0.4f)
            val iconAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)
            
            val iconAnimator = ObjectAnimator.ofPropertyValuesHolder(icon, iconScaleX, iconScaleY, iconAlpha).apply {
                duration = 700L
                interpolator = AnticipateInterpolator()
            }

            val textAlpha = ObjectAnimator.ofFloat(appNameView, View.ALPHA, 0f, 1f, 0f).apply {
                duration = 800L
            }
            
            val textTranslationY = ObjectAnimator.ofFloat(appNameView, View.TRANSLATION_Y, 150f, 50f).apply {
                duration = 800L
                interpolator = OvershootInterpolator()
            }

            val rootAlpha = ObjectAnimator.ofFloat(root, View.ALPHA, 1f, 0f).apply {
                duration = 400L
                startDelay = 500L
            }

            AnimatorSet().apply {
                playTogether(iconAnimator, textAlpha, textTranslationY, rootAlpha)
                doOnEnd { splashScreenView.remove() }
                start()
            }
        }

        setContent {
            LANTheme {
                AppNavigation()
            }
        }
    }
}