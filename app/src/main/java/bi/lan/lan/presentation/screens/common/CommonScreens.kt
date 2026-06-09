package bi.lan.lan.presentation.screens.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bi.lan.lan.presentation.components.LightningLogo
import bi.lan.lan.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNext: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )
    
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(PrimaryGreen, PrimaryGreenDark))),
        contentAlignment = Alignment.Center
    ) {
        // Subtle background patterns for "Pro" look
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = size.minDimension / 1.5f,
                center = center.copy(y = center.y - 200f)
            )
            drawCircle(
                color = Color.Black.copy(alpha = 0.05f),
                radius = size.minDimension / 2f,
                center = center.copy(x = size.width, y = size.height)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .graphicsLayer(
                    alpha = alphaAnim,
                    scaleX = scaleAnim,
                    scaleY = scaleAnim
                )
        ) {
            LightningLogo(size = 80, backgroundColor = SurfaceWhite)
            
            Spacer(Modifier.height(24.dp))
            
            Text(
                text = "LAN",
                style = MaterialTheme.typography.displaySmall,
                color = SurfaceWhite,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )
            
            Text(
                text = "LIGHTNING AGENT NETWORK",
                style = MaterialTheme.typography.labelLarge,
                color = SurfaceWhite.copy(alpha = 0.7f),
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Bottom Branding
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(alphaAnim),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = SurfaceWhite.copy(alpha = 0.3f),
                strokeWidth = 2.dp
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "SECURE • FAST • BORDERLESS",
                style = MaterialTheme.typography.labelSmall,
                color = SurfaceWhite.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun RoleSelectionScreen(onCustomer: () -> Unit, onAgent: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(PrimaryGreen, PrimaryGreenDark)))
    ) {
        // Subtle background patterns matching Splash
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.03f),
                radius = size.minDimension / 1.2f,
                center = center.copy(y = 0f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LightningLogo(size = 80, backgroundColor = SurfaceWhite.copy(alpha = 0.15f))
            
            Spacer(Modifier.height(32.dp))
            
            Text(
                "Welcome to LAN",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = SurfaceWhite,
                textAlign = TextAlign.Center
            )
            
            Spacer(Modifier.height(12.dp))
            
            Text(
                "Seamless Bitcoin Lightning payments for customers and agents.",
                style = MaterialTheme.typography.bodyLarge,
                color = SurfaceWhite.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(64.dp))

            // Customer button - White style
            Button(
                onClick = onCustomer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceWhite,
                    contentColor = PrimaryGreen
                ),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text(
                    "Continue as Customer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))

            // Agent button - Transparent/Glass style
            OutlinedButton(
                onClick = onAgent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceWhite.copy(alpha = 0.3f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SurfaceWhite
                )
            ) {
                Text(
                    "Continue as Agent",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(48.dp))
            
            Text(
                text = "SECURE • INSTANT • LOW FEE",
                style = MaterialTheme.typography.labelSmall,
                color = SurfaceWhite.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )
        }
    }
}
