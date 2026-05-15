package acj.soluciones.acjsignature.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import acj.soluciones.acjsignature.shared.ui.theme.DeepPurple
import acj.soluciones.acjsignature.shared.ui.theme.Error
import acj.soluciones.acjsignature.shared.ui.theme.Magenta
import acj.soluciones.acjsignature.shared.ui.theme.PinkLight
import acj.soluciones.acjsignature.shared.ui.theme.TextBody
import acj.soluciones.acjsignature.shared.ui.theme.TextMuted
import acj.soluciones.acjsignature.shared.ui.theme.White

/**
 * Pantalla de registro de usuario.
 * Incorpora la consulta automática de RENIEC mediante el DNI y tiene un diseño refinado.
 */
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: (String) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess(uiState.email)
        }
    }

    LaunchedEffect(uiState.registerError) {
        uiState.registerError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrors()
        }
    }

    LaunchedEffect(uiState.dniQueryError) {
        uiState.dniQueryError?.let {
            snackbarHostState.showSnackbar("DNI no encontrado o error en RENIEC: $it")
            viewModel.clearErrors()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(White, PinkLight.copy(alpha = 0.2f))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Barra Superior personalizada de Volver
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = DeepPurple
                        )
                    }
                    Text(
                        text = "Crear Cuenta",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepPurple,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Títulos centrales
                Text(
                    text = "Únete a ACJ Signature",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepPurple,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Completa tus datos para crear una cuenta de firma digital",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                // ── REGISTRATION FORM CARD ────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        
                        // Campo DNI con autocompletado
                        OutlinedTextField(
                            value = uiState.dni,
                            onValueChange = viewModel::onDniChanged,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            label = { Text("Número de DNI", style = MaterialTheme.typography.bodyMedium) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = "DNI",
                                    tint = Magenta,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                if (uiState.isDniLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Magenta
                                    )
                                }
                            },
                            singleLine = true,
                            isError = uiState.dniError != null,
                            supportingText = uiState.dniError?.let { { Text(it, color = Error) } },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Magenta,
                                focusedLabelColor = Magenta,
                                cursorColor = Magenta,
                                errorSupportingTextColor = Error
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Campo Nombres (Auto-llenado)
                        OutlinedTextField(
                            value = uiState.firstName,
                            onValueChange = viewModel::onFirstNameChanged,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            label = { Text("Nombres", style = MaterialTheme.typography.bodyMedium) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "FirstName",
                                    tint = Magenta,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            singleLine = true,
                            isError = uiState.firstNameError != null,
                            supportingText = uiState.firstNameError?.let { { Text(it, color = Error) } },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Magenta,
                                focusedLabelColor = Magenta,
                                cursorColor = Magenta,
                                errorSupportingTextColor = Error
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Campo Apellidos (Auto-llenado)
                        OutlinedTextField(
                            value = uiState.lastName,
                            onValueChange = viewModel::onLastNameChanged,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            label = { Text("Apellidos", style = MaterialTheme.typography.bodyMedium) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "LastName",
                                    tint = Magenta,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            singleLine = true,
                            isError = uiState.lastNameError != null,
                            supportingText = uiState.lastNameError?.let { { Text(it, color = Error) } },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Magenta,
                                focusedLabelColor = Magenta,
                                cursorColor = Magenta,
                                errorSupportingTextColor = Error
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Campo Correo Electrónico
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChanged,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            label = { Text("Correo Electrónico", style = MaterialTheme.typography.bodyMedium) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = Magenta,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            singleLine = true,
                            isError = uiState.emailError != null,
                            supportingText = uiState.emailError?.let { { Text(it, color = Error) } },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Magenta,
                                focusedLabelColor = Magenta,
                                cursorColor = Magenta,
                                errorSupportingTextColor = Error
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Campo Contraseña
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = viewModel::onPasswordChanged,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            label = { Text("Contraseña", style = MaterialTheme.typography.bodyMedium) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password",
                                    tint = Magenta,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = uiState.passwordError != null,
                            supportingText = {
                                if (uiState.passwordError != null) {
                                    Text(uiState.passwordError!!, color = Error)
                                } else {
                                    Text("La contraseña debe tener al menos una letra en mayuscula, una en minuscula y un digito.", color = TextMuted)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Magenta,
                                focusedLabelColor = Magenta,
                                cursorColor = Magenta,
                                errorSupportingTextColor = Error
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Campo Confirmar Contraseña
                        OutlinedTextField(
                            value = uiState.confirmPassword,
                            onValueChange = viewModel::onConfirmPasswordChanged,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            label = { Text("Confirmar Contraseña", style = MaterialTheme.typography.bodyMedium) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Confirm password",
                                    tint = Magenta,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            singleLine = true,
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = uiState.confirmPasswordError != null,
                            supportingText = uiState.confirmPasswordError?.let { { Text(it, color = Error) } },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Magenta,
                                focusedLabelColor = Magenta,
                                cursorColor = Magenta,
                                errorSupportingTextColor = Error
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Aceptar Términos
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.onAcceptTermsChanged(!uiState.acceptTerms) }
                        ) {
                            Checkbox(
                                checked = uiState.acceptTerms,
                                onCheckedChange = viewModel::onAcceptTermsChanged,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Magenta,
                                    checkmarkColor = White
                                )
                            )
                            Text(
                                text = "Acepto los Términos y Condiciones",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextBody
                            )
                        }

                        // Botón de Registro
                        if (uiState.isRegisterLoading) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Magenta)
                            }
                        } else {
                            ACJPrimaryButton(
                                text = "Crear Cuenta",
                                onClick = viewModel::register,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Enlace a Iniciar Sesión
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¿Ya tienes una cuenta? ",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextBody
                    )
                    Text(
                        text = "Iniciar Sesión",
                        style = MaterialTheme.typography.bodySmall,
                        color = Magenta,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))

                // Footer
                Text(
                    text = "DESARROLLADO POR ACJ SOLUCIONES",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 9.sp,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}
