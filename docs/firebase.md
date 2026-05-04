# Guía de Implementación: Firebase Authentication en Android con Jetpack Compose

**Proyecto:** App de Firma Digital ACJ  
**Stack:** Kotlin · Jetpack Compose · Hilt · Coroutines · Firebase Auth  
**Fecha:** Abril 2026

---

## Tabla de Contenidos

1. [Configuración del Proyecto Firebase](#1-configuración-del-proyecto-firebase)
2. [Dependencias](#2-dependencias)
3. [Estructura de Archivos](#3-estructura-de-archivos)
4. [Capa Domain](#4-capa-domain)
5. [Capa Data](#5-capa-data)
6. [Inyección de Dependencias](#6-inyección-de-dependencias)
7. [Capa Presentation — Login](#7-capa-presentation--login)
8. [Capa Presentation — Register](#8-capa-presentation--register)
9. [Navegación y sesión persistente](#9-navegación-y-sesión-persistente)
10. [Integración con Biometría (opcional)](#10-integración-con-biometría-opcional)
11. [Manejo de errores de Firebase](#11-manejo-de-errores-de-firebase)
12. [Checklist Final](#12-checklist-final)

---

## 1. Configuración del Proyecto Firebase

### Paso 1 — Crear proyecto en Firebase Console

1. Ir a [https://console.firebase.google.com](https://console.firebase.google.com)
2. **Add project** → Asignar nombre (ej. `acj-firma-digital`)
3. Desactivar Google Analytics si no se necesita
4. **Continue** → proyecto creado

### Paso 2 — Registrar la app Android

1. En el proyecto, hacer clic en el ícono de Android
2. Ingresar el **package name** exacto: `com.acj.firmapp`
3. Ingresar un apodo (ej. `ACJ Firma Android`)
4. Ingresar el **SHA-1** del keystore de debug:

```bash
# En terminal, desde la raíz del proyecto
./gradlew signingReport

# O directamente con keytool
keytool -list -v \
  -alias androiddebugkey \
  -keystore ~/.android/debug.keystore \
  -storepass android \
  -keypass android
```

5. Descargar `google-services.json` y colocarlo en `app/` (mismo nivel que `build.gradle` del módulo)

### Paso 3 — Activar Email/Password en Firebase Console

1. Authentication → Sign-in method
2. **Email/Password** → Enable → Save

> También puedes activar **Google Sign-In** aquí si lo necesitas en el futuro.

---

## 2. Dependencias

### `build.gradle` raíz

```groovy
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.4.1'
    }
}

plugins {
    id 'com.android.application' version '8.2.2' apply false
    id 'com.google.gms.google-services' version '4.4.1' apply false
    id 'com.google.dagger.hilt.android' version '2.50' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.22' apply false
}
```

### `app/build.gradle`

```groovy
plugins {
    id 'com.android.application'
    id 'com.google.gms.google-services'   // ← obligatorio para Firebase
    id 'com.google.dagger.hilt.android'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-kapt'
}

android {
    namespace 'com.acj.firmapp'
    compileSdk 34

    defaultConfig {
        minSdk 26
        targetSdk 34
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = '17' }

    buildFeatures { compose true }
    composeOptions { kotlinCompilerExtensionVersion '1.5.8' }
}

dependencies {
    // ─── Firebase ──────────────────────────────────────────────────────────
    implementation platform('com.google.firebase:firebase-bom:32.7.2')
    implementation 'com.google.firebase:firebase-auth-ktx'

    // ─── Jetpack Compose ───────────────────────────────────────────────────
    implementation platform('androidx.compose:compose-bom:2024.02.00')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.material3:material3'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.activity:activity-compose:1.8.2'
    implementation 'androidx.navigation:navigation-compose:2.7.6'

    // ─── Hilt ──────────────────────────────────────────────────────────────
    implementation 'com.google.dagger:hilt-android:2.50'
    kapt 'com.google.dagger:hilt-android-compiler:2.50'
    implementation 'androidx.hilt:hilt-navigation-compose:1.1.0'

    // ─── Lifecycle / ViewModel ─────────────────────────────────────────────
    implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'

    // ─── Biometría (opcional) ──────────────────────────────────────────────
    implementation 'androidx.biometric:biometric:1.1.0'

    // ─── Coroutines para Firebase ──────────────────────────────────────────
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3'
}
```

> `kotlinx-coroutines-play-services` permite usar `.await()` en los `Task<T>` de Firebase.

---

## 3. Estructura de Archivos

```
com/acj/firmapp/
│
├── FirmApp.kt                              ← Application class (Hilt)
│
├── domain/
│   └── auth/
│       ├── model/
│       │   └── Usuario.kt                  ← modelo de dominio
│       ├── repository/
│       │   └── AuthRepository.kt           ← interfaz (sin imports Firebase)
│       └── usecase/
│           ├── LoginUseCase.kt
│           ├── RegisterUseCase.kt
│           ├── LogoutUseCase.kt
│           └── GetCurrentUserUseCase.kt
│
├── data/
│   └── auth/
│       └── repository/
│           └── AuthRepositoryImpl.kt       ← única clase que toca FirebaseAuth
│
├── presentation/
│   └── auth/
│       ├── login/
│       │   ├── LoginScreen.kt
│       │   ├── LoginViewModel.kt
│       │   └── LoginState.kt
│       └── register/
│           ├── RegisterScreen.kt
│           ├── RegisterViewModel.kt
│           └── RegisterState.kt
│
├── di/
│   └── AuthModule.kt
│
├── navigation/
│   ├── AppNavGraph.kt
│   └── Screen.kt
│
└── shared/
    └── domain/
        └── Result.kt
```

---

## 4. Capa Domain

### `shared/domain/Result.kt`

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()

    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Throwable) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }
}
```

### `domain/auth/model/Usuario.kt`

```kotlin
data class Usuario(
    val uid: String,
    val email: String,
    val nombre: String?
)
```

### `domain/auth/repository/AuthRepository.kt`

```kotlin
// Sin ningún import de Firebase — solo tipos de dominio
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Usuario>
    suspend fun register(email: String, password: String, nombre: String): Result<Usuario>
    suspend fun logout()
    fun getCurrentUser(): Usuario?
}
```

### `domain/auth/usecase/LoginUseCase.kt`

```kotlin
class LoginUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): Result<Usuario> {
        if (email.isBlank()) return Result.Error(IllegalArgumentException("El email es requerido"))
        if (password.length < 6) return Result.Error(IllegalArgumentException("La contraseña debe tener al menos 6 caracteres"))
        return authRepository.login(email.trim(), password)
    }
}
```

### `domain/auth/usecase/RegisterUseCase.kt`

```kotlin
class RegisterUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
        nombre: String
    ): Result<Usuario> {
        if (nombre.isBlank()) return Result.Error(IllegalArgumentException("El nombre es requerido"))
        if (email.isBlank()) return Result.Error(IllegalArgumentException("El email es requerido"))
        if (password.length < 6) return Result.Error(IllegalArgumentException("La contraseña debe tener al menos 6 caracteres"))
        if (password != confirmPassword) return Result.Error(IllegalArgumentException("Las contraseñas no coinciden"))
        return authRepository.register(email.trim(), password, nombre.trim())
    }
}
```

### `domain/auth/usecase/LogoutUseCase.kt`

```kotlin
class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke() = authRepository.logout()
}
```

### `domain/auth/usecase/GetCurrentUserUseCase.kt`

```kotlin
class GetCurrentUserUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(): Usuario? = authRepository.getCurrentUser()
}
```

---

## 5. Capa Data

### `data/auth/repository/AuthRepositoryImpl.kt`

```kotlin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Usuario> {
        return try {
            val result = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = result.user
                ?: return Result.Error(Exception("No se pudo obtener el usuario"))

            Result.Success(
                Usuario(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    nombre = firebaseUser.displayName
                )
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        nombre: String
    ): Result<Usuario> {
        return try {
            // 1. Crear cuenta
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = result.user
                ?: return Result.Error(Exception("No se pudo crear el usuario"))

            // 2. Actualizar display name
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(nombre)
                .build()
            firebaseUser.updateProfile(profileUpdate).await()

            Result.Success(
                Usuario(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    nombre = nombre
                )
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): Usuario? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        return Usuario(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            nombre = firebaseUser.displayName
        )
    }
}
```

---

## 6. Inyección de Dependencias

### `FirmApp.kt`

```kotlin
@HiltAndroidApp
class FirmApp : Application()
```

Registrar en `AndroidManifest.xml`:

```xml
<application
    android:name=".FirmApp"
    ...>
```

### `di/AuthModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth
    ): AuthRepository = AuthRepositoryImpl(firebaseAuth)

    @Provides
    fun provideLoginUseCase(repo: AuthRepository) = LoginUseCase(repo)

    @Provides
    fun provideRegisterUseCase(repo: AuthRepository) = RegisterUseCase(repo)

    @Provides
    fun provideLogoutUseCase(repo: AuthRepository) = LogoutUseCase(repo)

    @Provides
    fun provideGetCurrentUserUseCase(repo: AuthRepository) = GetCurrentUserUseCase(repo)
}
```

---

## 7. Capa Presentation — Login

### `presentation/auth/login/LoginState.kt`

```kotlin
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginExitoso: Boolean = false
)
```

### `presentation/auth/login/LoginViewModel.kt`

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    fun onEmailChange(value: String) {
        state = state.copy(email = value, error = null)
    }

    fun onPasswordChange(value: String) {
        state = state.copy(password = value, error = null)
    }

    fun login() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            loginUseCase(state.email, state.password)
                .onSuccess {
                    state = state.copy(isLoading = false, loginExitoso = true)
                }
                .onError { error ->
                    state = state.copy(
                        isLoading = false,
                        error = mapFirebaseError(error)
                    )
                }
        }
    }

    private fun mapFirebaseError(e: Throwable): String = when {
        e.message?.contains("no user record") == true ->
            "No existe una cuenta con ese email"
        e.message?.contains("password is invalid") == true ->
            "Contraseña incorrecta"
        e.message?.contains("badly formatted") == true ->
            "El formato del email no es válido"
        e.message?.contains("network error") == true ->
            "Sin conexión. Verifica tu internet"
        e is IllegalArgumentException -> e.message ?: "Datos inválidos"
        else -> "Error al iniciar sesión. Intenta de nuevo"
    }
}
```

### `presentation/auth/login/LoginScreen.kt`

```kotlin
@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state = viewModel.state

    LaunchedEffect(state.loginExitoso) {
        if (state.loginExitoso) onLoginExitoso()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Firma Digital ACJ",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        var passwordVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (passwordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { viewModel.login() }),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Error message
        AnimatedVisibility(visible = state.error != null) {
            state.error?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = viewModel::login,
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Iniciar sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onIrARegistro) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
```

---

## 8. Capa Presentation — Register

### `presentation/auth/register/RegisterState.kt`

```kotlin
data class RegisterState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val registroExitoso: Boolean = false
)
```

### `presentation/auth/register/RegisterViewModel.kt`

```kotlin
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    var state by mutableStateOf(RegisterState())
        private set

    fun onNombreChange(value: String) { state = state.copy(nombre = value, error = null) }
    fun onEmailChange(value: String) { state = state.copy(email = value, error = null) }
    fun onPasswordChange(value: String) { state = state.copy(password = value, error = null) }
    fun onConfirmPasswordChange(value: String) { state = state.copy(confirmPassword = value, error = null) }

    fun register() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            registerUseCase(
                email = state.email,
                password = state.password,
                confirmPassword = state.confirmPassword,
                nombre = state.nombre
            )
                .onSuccess {
                    state = state.copy(isLoading = false, registroExitoso = true)
                }
                .onError { error ->
                    state = state.copy(
                        isLoading = false,
                        error = mapFirebaseError(error)
                    )
                }
        }
    }

    private fun mapFirebaseError(e: Throwable): String = when {
        e.message?.contains("email address is already in use") == true ->
            "Ya existe una cuenta con ese email"
        e.message?.contains("badly formatted") == true ->
            "El formato del email no es válido"
        e.message?.contains("network error") == true ->
            "Sin conexión. Verifica tu internet"
        e is IllegalArgumentException -> e.message ?: "Datos inválidos"
        else -> "Error al registrarse. Intenta de nuevo"
    }
}
```

### `presentation/auth/register/RegisterScreen.kt`

```kotlin
@Composable
fun RegisterScreen(
    onRegistroExitoso: () -> Unit,
    onVolver: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state = viewModel.state

    LaunchedEffect(state.registroExitoso) {
        if (state.registroExitoso) onRegistroExitoso()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.nombre,
            onValueChange = viewModel::onNombreChange,
            label = { Text("Nombre completo") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Correo electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        var passwordVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (passwordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = { Text("Confirmar contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { viewModel.register() }),
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(visible = state.error != null) {
            state.error?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = viewModel::register,
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Crear cuenta")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onVolver, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}
```

---

## 9. Navegación y sesión persistente

### `navigation/Screen.kt`

```kotlin
sealed class Screen(val route: String) {
    object Login    : Screen("auth/login")
    object Register : Screen("auth/register")
    object Home     : Screen("home")
    object Firma    : Screen("firma")
}
```

### `navigation/AppNavGraph.kt`

```kotlin
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    getCurrentUser: GetCurrentUserUseCase
) {
    // Determinar destino inicial según sesión activa
    val startDestination = if (getCurrentUser() != null) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onIrARegistro = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegistroExitoso = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onCerrarSesion = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
```

> Firebase Auth **persiste la sesión automáticamente**. `getCurrentUser()` devuelve el usuario aunque la app se reinicie, hasta que el usuario haga logout explícito.

### `MainActivity.kt`

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var getCurrentUser: GetCurrentUserUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirmAppTheme {
                AppNavGraph(getCurrentUser = getCurrentUser)
            }
        }
    }
}
```

---

## 10. Integración con Biometría (opcional)

Combina Firebase Auth + Biometría: el usuario autentica con Firebase la primera vez, y en siguientes aperturas solo usa huella/face unlock para desbloquear la sesión ya activa.

```kotlin
// En HomeScreen o en un AuthGuard composable
@Composable
fun BiometricGuard(
    onAutenticado: () -> Unit,
    onFallback: () -> Unit,     // Ir al login normal
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var autenticado by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val biometricManager = BiometricManager.from(context)

        val canAuthenticate = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )

        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            // Dispositivo sin biometría — saltar directo
            autenticado = true
            return@LaunchedEffect
        }

        val executor = ContextCompat.getMainExecutor(context)
        val prompt = BiometricPrompt(
            context as FragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    autenticado = true
                    onAutenticado()
                }
                override fun onAuthenticationError(code: Int, msg: CharSequence) {
                    onFallback()
                }
                override fun onAuthenticationFailed() { /* huella no reconocida */ }
            }
        )

        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Firma Digital ACJ")
                .setSubtitle("Confirma tu identidad para continuar")
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                .build()
        )
    }

    if (autenticado) content()
}
```

**Flujo completo con biometría:**

```
App abre
    ↓
¿Hay sesión Firebase activa? (getCurrentUser() != null)
    ↓ Sí                              ↓ No
Pedir biometría               → LoginScreen / RegisterScreen
    ↓ OK
HomeScreen / FirmaScreen
```

---

## 11. Manejo de errores de Firebase

Firebase lanza `FirebaseAuthException` con códigos específicos. Una forma más robusta de mapearlos:

```kotlin
import com.google.firebase.auth.FirebaseAuthException

fun mapearErrorFirebase(e: Throwable): String {
    if (e is FirebaseAuthException) {
        return when (e.errorCode) {
            "ERROR_USER_NOT_FOUND"        -> "No existe una cuenta con ese email"
            "ERROR_WRONG_PASSWORD"        -> "Contraseña incorrecta"
            "ERROR_INVALID_EMAIL"         -> "El formato del email no es válido"
            "ERROR_EMAIL_ALREADY_IN_USE"  -> "Ya existe una cuenta con ese email"
            "ERROR_WEAK_PASSWORD"         -> "La contraseña es muy débil (mínimo 6 caracteres)"
            "ERROR_TOO_MANY_REQUESTS"     -> "Demasiados intentos. Espera unos minutos"
            "ERROR_NETWORK_REQUEST_FAILED"-> "Sin conexión. Verifica tu internet"
            "ERROR_USER_DISABLED"         -> "Esta cuenta ha sido deshabilitada"
            else -> "Error de autenticación (${e.errorCode})"
        }
    }
    if (e is IllegalArgumentException) return e.message ?: "Datos inválidos"
    return "Error inesperado. Intenta de nuevo"
}
```

Reemplaza el `mapFirebaseError` en ambos ViewModels con esta función para mayor precisión.

---

## 12. Checklist Final

```
FIREBASE CONSOLE
  [ ] Proyecto creado en Firebase Console
  [ ] App Android registrada con package name correcto
  [ ] google-services.json colocado en app/
  [ ] SHA-1 del keystore de debug agregado
  [ ] Email/Password habilitado en Authentication → Sign-in method

DEPENDENCIAS
  [ ] firebase-bom y firebase-auth-ktx en build.gradle
  [ ] google-services plugin aplicado en ambos build.gradle
  [ ] kotlinx-coroutines-play-services agregado
  [ ] Hilt configurado (plugin + Application class)

CÓDIGO
  [ ] FirmApp.kt con @HiltAndroidApp registrada en Manifest
  [ ] AuthRepository (interfaz) sin imports de Firebase
  [ ] AuthRepositoryImpl con FirebaseAuth inyectado por Hilt
  [ ] LoginUseCase y RegisterUseCase con validaciones de dominio
  [ ] LoginViewModel y RegisterViewModel usando viewModelScope
  [ ] Errores Firebase mapeados a mensajes en español
  [ ] Sesión persistente verificada en AppNavGraph (startDestination)

NAVEGACIÓN
  [ ] Login → Home (con popUpTo para limpiar back stack)
  [ ] Register → Home (con popUpTo para limpiar back stack)
  [ ] Logout → Login (con popUpTo para limpiar back stack)

PRUEBAS MANUALES
  [ ] Registro de usuario nuevo funciona
  [ ] Login con credenciales correctas funciona
  [ ] Login con contraseña incorrecta muestra mensaje correcto
  [ ] Login con email no registrado muestra mensaje correcto
  [ ] Cerrar app y reabrir mantiene la sesión activa
  [ ] Logout cierra la sesión y redirige al Login
  [ ] Sin conexión a internet muestra mensaje de error correcto
```

---

*Guía generada para el proyecto ACJ Firma Digital — Abril 2026*