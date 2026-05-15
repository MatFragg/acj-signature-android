package acj.soluciones.acjsignature.presentation.changepassword

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
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }

    /*
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack() // O mostrar un toast
        }
    }
    */

    LaunchedEffect(uiState.error, uiState.oldPasswordError, uiState.newPasswordError) {
        if (uiState.error != null || uiState.oldPasswordError != null || uiState.newPasswordError != null) {
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
            contentDescription = "Change Password",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Cambiar Contraseña",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ingresa tu contraseña actual y la nueva contraseña que deseas utilizar.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.oldPassword,
            onValueChange = viewModel::onOldPasswordChanged,
            textStyle = MaterialTheme.typography.bodyMedium,
            label = { Text("Contraseña Actual", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Magenta)
            },
            trailingIcon = {
                IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                    Icon(
                        imageVector = if (oldPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        tint = Color.Gray
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = uiState.oldPasswordError != null,
            supportingText = uiState.oldPasswordError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Magenta,
                focusedLabelColor = Magenta,
                cursorColor = Magenta,
                errorSupportingTextColor = MaterialTheme.colorScheme.error
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
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
                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                    Icon(
                        imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        tint = Color.Gray
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
            text = "Volver a Ajustes",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onNavigateBack() }
        )
    }
}
