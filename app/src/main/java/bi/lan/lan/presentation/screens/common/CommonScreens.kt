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

import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import bi.lan.lan.R

@Composable
fun SplashScreen(onNext: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )
    
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000)
        onNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        // Professional background patterns
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = PrimaryGreen.copy(alpha = 0.15f),
                radius = size.minDimension / 0.8f,
                center = center.copy(y = -200f)
            )
            drawCircle(
                color = PrimaryGreenDark.copy(alpha = 0.1f),
                radius = size.minDimension / 1.2f,
                center = center.copy(x = size.width + 100f, y = size.height + 100f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(bottom = 100.dp) // Adjusted to feel more balanced between top and bottom
                .graphicsLayer(
                    alpha = alphaAnim,
                    scaleX = scaleAnim,
                    scaleY = scaleAnim
                )
        ) {
            // Reusing logo1 with a "pro" look, ensuring it fills nicely without being cut
            Surface(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape),
                color = SurfaceWhite.copy(alpha = 0.05f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo1),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp), // Safe padding to ensure it fits the circle "pro" look
                    contentScale = ContentScale.Fit
                )
            }
            
            Spacer(Modifier.height(32.dp))
            
            Text(
                text = stringResource(id = R.string.app_name).uppercase(),
                style = MaterialTheme.typography.displaySmall,
                color = SurfaceWhite,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp
            )
            
            Text(
                text = "LIGHTNING AGENT NETWORK",
                style = MaterialTheme.typography.labelLarge,
                color = PrimaryGreen,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // Bottom Branding - Pro layout
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .alpha(alphaAnim),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .width(120.dp)
                    .height(2.dp)
                    .clip(CircleShape),
                color = PrimaryGreen,
                trackColor = SurfaceWhite.copy(alpha = 0.1f)
            )
            Spacer(Modifier.height(32.dp))
            Text(
                text = "SECURE • FAST • BORDERLESS",
                style = MaterialTheme.typography.labelSmall,
                color = SurfaceWhite.copy(alpha = 0.4f),
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RoleSelectionScreen(onCustomer: () -> Unit, onAgent: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Matching "Pro" background patterns
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = PrimaryGreen.copy(alpha = 0.05f),
                radius = size.minDimension / 1f,
                center = center.copy(x = 0f, y = 0f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                color = SurfaceWhite.copy(alpha = 0.05f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.2f))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo1),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
            
            Spacer(Modifier.height(32.dp))
            
            Text(
                stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = SurfaceWhite,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp
            )
            
            Spacer(Modifier.height(12.dp))
            
            Text(
                "Seamless Bitcoin Lightning payments for customers and agents.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondaryDark,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(64.dp))

            // Customer button - Primary Green style
            Button(
                onClick = onCustomer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = BackgroundDark
                ),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text(
                    "Continue as Customer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.height(20.dp))

            // Agent button - Transparent/Glass style
            OutlinedButton(
                onClick = onAgent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryGreen
                )
            ) {
                Text(
                    "Continue as Agent",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(64.dp))
            
            Text(
                text = "SECURE • INSTANT • LOW FEE",
                style = MaterialTheme.typography.labelSmall,
                color = SurfaceWhite.copy(alpha = 0.3f),
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp
            )
        }
    }
}
