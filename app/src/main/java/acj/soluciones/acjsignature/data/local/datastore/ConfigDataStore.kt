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

/**
 * Modelo de datos que representa las preferencias de configuración para la firma digital.
 *
 * @property logoUri URI local de la imagen utilizada como logo en el sello.
 * @property incluirEmpresa Indica si se muestra la empresa en el sello visual.
 * @property incluirCargo Indica si se muestra el cargo en el sello visual.
 * @property incluirTelefono Indica si se muestra el teléfono en el sello visual.
 * @property usarTslPrueba Define si se deben validar certificados contra una TSL de pruebas.
 * @property nombreUsuario Nombre personalizado para el titular.
 * @property cargoUsuario Cargo personalizado para el titular.
 * @property empresaUsuario Empresa personalizada para el titular.
 * @property telefonoUsuario Teléfono personalizado para el titular.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
data class ConfiguracionFirma(
    val logoUri: String? = null,
    val incluirEmpresa: Boolean = true,
    val incluirCargo: Boolean = true,
    val incluirTelefono: Boolean = false,
    val usarTslPrueba: Boolean = false,
    val tslUrl: String = "https://nodoyuna4.github.io/pki/tsl/tsl2026.xml",
    val nombreUsuario: String = "",
    val cargoUsuario: String = "",
    val empresaUsuario: String = "",
    val telefonoUsuario: String = "",
)

/**
 * Gestor de preferencias persistentes utilizando Jetpack DataStore.
 * Almacena configuraciones de usuario que afectan el comportamiento y la apariencia de la firma.
 *
 * @property context Contexto para acceder al DataStore.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
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
        val USAR_TSL_PRUEBA = booleanPreferencesKey("usar_tsl_prueba")
        val TSL_URL = stringPreferencesKey("tsl_url")
    }

    /**
     * Flujo reactivo que emite los cambios en la configuración almacenada.
     */
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
            usarTslPrueba = prefs[Keys.USAR_TSL_PRUEBA] ?: false,
            tslUrl = prefs[Keys.TSL_URL] ?: "https://nodoyuna4.github.io/pki/tsl/tsl2026.xml",
        )
    }

    /**
     * Guarda un objeto de configuración completo en el almacenamiento.
     *
     * @param config Objeto con todos los valores a persistir.
     */
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
            prefs[Keys.USAR_TSL_PRUEBA] = config.usarTslPrueba
            prefs[Keys.TSL_URL] = config.tslUrl
        }
    }

    /**
     * Actualiza únicamente la referencia al logo visual.
     *
     * @param uri Nueva URI del logo o null para eliminarlo.
     */
    suspend fun actualizarLogo(uri: String?) {
        context.dataStore.edit { prefs ->
            uri?.let { prefs[Keys.LOGO_URI] = it } ?: prefs.remove(Keys.LOGO_URI)
        }
    }

    /**
     * Actualiza selectivamente los interruptores de visibilidad en el sello.
     *
     * @param empresa Visibilidad de la empresa.
     * @param cargo Visibilidad del cargo.
     * @param telefono Visibilidad del teléfono.
     */
    suspend fun actualizarCampos(empresa: Boolean, cargo: Boolean, telefono: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.INCLUIR_EMPRESA] = empresa
            prefs[Keys.INCLUIR_CARGO] = cargo
            prefs[Keys.INCLUIR_TELEFONO] = telefono
        }
    }
}
