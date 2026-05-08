package acj.soluciones.acjsignature.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import acj.soluciones.acjsignature.data.remote.dto.response.AuthResponse
import acj.soluciones.acjsignature.shared.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore by preferencesDataStore(name = Constants.SESSION_DATASTORE_NAME)

/**
 * Gestor de la sesión persistente del usuario utilizando Jetpack DataStore.
 * Almacena el token JWT de autenticación y los datos del perfil del usuario actual.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val TOKEN = stringPreferencesKey("session_token")
        val TOKEN_TYPE = stringPreferencesKey("session_token_type")
        val EXPIRES_IN = longPreferencesKey("session_expires_in")
        
        // Datos del Usuario
        val USER_ID = longPreferencesKey("user_id")

        val EMAIL = stringPreferencesKey("user_email")
        val DNI = stringPreferencesKey("user_dni")
        val FIRST_NAME = stringPreferencesKey("user_first_name")
        val LAST_NAME = stringPreferencesKey("user_last_name")
        val FULL_NAME = stringPreferencesKey("user_full_name")
        val ACTIVE = booleanPreferencesKey("user_active")
        val ROLES = stringSetPreferencesKey("user_roles")
    }

    /**
     * Flujo reactivo que emite si el usuario está logueado (tiene un token guardado).
     */
    val isLoggedIn: Flow<Boolean> = context.sessionDataStore.data.map { prefs ->
        !prefs[Keys.TOKEN].isNullOrBlank()
    }

    /**
     * Flujo reactivo que emite el token de autenticación.
     */
    val token: Flow<String?> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.TOKEN]
    }

    /**
     * Guarda la sesión del usuario tras un login o registro exitoso.
     */
    suspend fun saveSession(authResponse: AuthResponse) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.TOKEN] = authResponse.token
            prefs[Keys.TOKEN_TYPE] = authResponse.tokenType
            prefs[Keys.EXPIRES_IN] = authResponse.expiresIn
            
            val user = authResponse.user
            prefs[Keys.USER_ID] = user.id

            prefs[Keys.EMAIL] = user.email
            user.dni?.let { prefs[Keys.DNI] = it }
            user.firstName?.let { prefs[Keys.FIRST_NAME] = it }
            user.lastName?.let { prefs[Keys.LAST_NAME] = it }
            user.fullName?.let { prefs[Keys.FULL_NAME] = it }
            user.active?.let { prefs[Keys.ACTIVE] = it }
            user.roles?.let { prefs[Keys.ROLES] = it }
        }
    }

    /**
     * Borra los datos de la sesión actual (Logout).
     */
    suspend fun clearSession() {
        context.sessionDataStore.edit { prefs ->
            prefs.clear()
        }
    }

    /**
     * Obtiene el nombre completo del usuario autenticado de forma síncrona/flujo.
     */
    val userFullName: Flow<String> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.FULL_NAME] ?: prefs[Keys.EMAIL] ?: "Usuario"
    }

    /**
     * Obtiene el DNI del usuario autenticado de forma reactiva.
     */
    val userDni: Flow<String> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.DNI] ?: ""
    }
}
