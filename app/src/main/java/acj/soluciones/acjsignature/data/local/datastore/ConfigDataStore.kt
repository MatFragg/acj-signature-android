package acj.soluciones.acjsignature.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "acj_preferences")

data class ConfiguracionFirma(
    val logoUri: String? = null,
    val incluirEmpresa: Boolean = true,
    val incluirCargo: Boolean = true,
    val incluirTelefono: Boolean = false,
    val nombreUsuario: String = "",
    val cargoUsuario: String = "",
    val empresaUsuario: String = "",
    val telefonoUsuario: String = "",
)

@Singleton
class ConfigDataStore @Inject constructor(
    private val context: Context,
) {
    private object Keys {
        val LOGO_URI = stringPreferencesKey("logo_uri")
        val INCLUIR_EMPRESA = booleanPreferencesKey("incluir_empresa")
        val INCLUIR_CARGO = booleanPreferencesKey("incluir_cargo")
        val INCLUIR_TELEFONO = booleanPreferencesKey("incluir_telefono")
        val NOMBRE_USUARIO = stringPreferencesKey("nombre_usuario")
        val CARGO_USUARIO = stringPreferencesKey("cargo_usuario")
        val EMPRESA_USUARIO = stringPreferencesKey("empresa_usuario")
        val TELEFONO_USUARIO = stringPreferencesKey("telefono_usuario")
    }

    val configuracion: Flow<ConfiguracionFirma> = context.dataStore.data.map { prefs ->
        ConfiguracionFirma(
            logoUri = prefs[Keys.LOGO_URI],
            incluirEmpresa = prefs[Keys.INCLUIR_EMPRESA] ?: true,
            incluirCargo = prefs[Keys.INCLUIR_CARGO] ?: true,
            incluirTelefono = prefs[Keys.INCLUIR_TELEFONO] ?: false,
            nombreUsuario = prefs[Keys.NOMBRE_USUARIO] ?: "",
            cargoUsuario = prefs[Keys.CARGO_USUARIO] ?: "",
            empresaUsuario = prefs[Keys.EMPRESA_USUARIO] ?: "",
            telefonoUsuario = prefs[Keys.TELEFONO_USUARIO] ?: "",
        )
    }

    suspend fun guardar(config: ConfiguracionFirma) {
        context.dataStore.edit { prefs ->
            config.logoUri?.let { prefs[Keys.LOGO_URI] = it } ?: prefs.remove(Keys.LOGO_URI)
            prefs[Keys.INCLUIR_EMPRESA] = config.incluirEmpresa
            prefs[Keys.INCLUIR_CARGO] = config.incluirCargo
            prefs[Keys.INCLUIR_TELEFONO] = config.incluirTelefono
            prefs[Keys.NOMBRE_USUARIO] = config.nombreUsuario
            prefs[Keys.CARGO_USUARIO] = config.cargoUsuario
            prefs[Keys.EMPRESA_USUARIO] = config.empresaUsuario
            prefs[Keys.TELEFONO_USUARIO] = config.telefonoUsuario
        }
    }

    suspend fun actualizarLogo(uri: String?) {
        context.dataStore.edit { prefs ->
            uri?.let { prefs[Keys.LOGO_URI] = it } ?: prefs.remove(Keys.LOGO_URI)
        }
    }

    suspend fun actualizarCampos(empresa: Boolean, cargo: Boolean, telefono: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.INCLUIR_EMPRESA] = empresa
            prefs[Keys.INCLUIR_CARGO] = cargo
            prefs[Keys.INCLUIR_TELEFONO] = telefono
        }
    }
}
