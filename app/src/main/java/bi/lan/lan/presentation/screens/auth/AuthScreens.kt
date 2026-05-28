package bi.lan.lan.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bi.lan.lan.presentation.components.LoadingButton
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onNavigateToLogin()
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Lightning Agent", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel(), onNavigateToOtp: (String) -> Unit) {
    val phone by viewModel.phoneNumber.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val success by viewModel.success.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(success) {
        if (success) {
            onNavigateToOtp(phone)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome Back", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Enter your phone number to continue", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = phone,
            onValueChange = { viewModel.updatePhoneNumber(it) },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        
        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        LoadingButton("Continue", isLoading) {
            viewModel.login()
        }
    }
}

@Composable
fun OtpScreen(phone: String, viewModel: OtpViewModel = koinViewModel(), onNavigateToHome: () -> Unit) {
    val otp by viewModel.otp.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val user by viewModel.user.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(user) {
        if (user != null) {
            onNavigateToHome()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Verify Phone", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Enter the OTP sent to $phone", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = otp,
            onValueChange = { viewModel.updateOtp(it) },
            label = { Text("OTP (use 1234 for mock)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        LoadingButton("Verify & Login", isLoading) {
            viewModel.verifyOtp(phone)
        }
    }
}
