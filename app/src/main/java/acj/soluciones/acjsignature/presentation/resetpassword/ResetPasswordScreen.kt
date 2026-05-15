package acj.soluciones.acjsignature.presentation.resetpassword

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import acj.soluciones.acjsignature.shared.ui.components.ACJPrimaryButton
import acj.soluciones.acjsignature.shared.ui.theme.Magenta

@Composable
fun ResetPasswordScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var passwordVisible by remember { mutableStateOf(false) }

    /*
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateToLogin()
        }
    }
    */

    LaunchedEffect(uiState.error, uiState.otpError, uiState.newPasswordError) {
        if (uiState.error != null || uiState.otpError != null || uiState.newPasswordError != null) {
            kotlinx.coroutines.delay(4000)
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Icon(
            imageVector = Icons.Default.Password,
            contentDescription = "Reset Password",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Nueva Contraseña",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ingresa el código OTP enviado a tu correo electrónico y tu nueva contraseña.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.otp,
            onValueChange = viewModel::onOtpChanged,
            textStyle = MaterialTheme.typography.bodyMedium,
            label = { Text("Código OTP", style = MaterialTheme.typography.bodyMedium) },
            singleLine = true,
            isError = uiState.otpError != null,
            supportingText = uiState.otpError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Magenta,
                focusedLabelColor = Magenta,
                cursorColor = Magenta,
                errorSupportingTextColor = MaterialTheme.colorScheme.error
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.newPassword,
            onValueChange = viewModel::onNewPasswordChanged,
            textStyle = MaterialTheme.typography.bodyMedium,
            label = { Text("Nueva Contraseña", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Magenta)
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        tint = Color.Gray
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = uiState.newPasswordError != null,
            supportingText = uiState.newPasswordError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Magenta,
                focusedLabelColor = Magenta,
                cursorColor = Magenta,
                errorSupportingTextColor = MaterialTheme.colorScheme.error
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        uiState.error?.let { errorMsg ->
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        uiState.successMessage?.let { successMsg ->
            Text(
                text = successMsg,
                color = Color(0xFF4CAF50), // Verde
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            ACJPrimaryButton(
                text = "Cambiar Contraseña",
                onClick = viewModel::submit,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (uiState.isSuccess) "Volver al Login" else "Cancelar",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onNavigateToLogin() }
        )
    }
}
