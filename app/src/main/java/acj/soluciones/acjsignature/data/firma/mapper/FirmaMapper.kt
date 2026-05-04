package acj.soluciones.acjsignature.data.firma.mapper

import acj.soluciones.acjsignature.domain.model.Certificado
import acj.soluciones.acjsignature.domain.model.DocumentoFirma
import acj.soluciones.acjsignature.domain.model.ResultadoValidacion
import acj.soluciones.acjsignature.domain.model.ResultadoFirma
import acj.soluciones.acjsignature.domain.model.ResultadoValidacionFirma
import android.content.Context
import com.acj.firma.util.Parameters
import java.security.cert.X509Certificate
import com.acj.firma.controller.ValidacionController

/**
 * Objeto encargado de la transformación de datos entre las distintas capas de la aplicación.
 * Mapea objetos nativos de certificados y de la biblioteca nativa acjfirmalib hacia modelos de dominio y parámetros de ejecución.
 *
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
object FirmaMapper {

    /**
     * Mapea un certificado X.509 nativo de Java hacia el modelo de dominio Certificado.
     * Extrae campos específicos como el nombre común (CN), organización y el bit de no repudio.
     *
     * @param alias Identificador único asignado al certificado.
     * @return Objeto Certificado con los metadatos extraídos.
     */
    fun X509Certificate.toCertificado(alias: String): Certificado {
        val cn = com.acj.firma.lib.LibUtilitario.extractField(this, "2.5.4.3") ?: alias
        val org = com.acj.firma.lib.LibUtilitario.extractField(this, "2.5.4.10") ?: ""
        val issuerCn = com.acj.firma.lib.LibUtilitario.extractIssuerField(this, "2.5.4.10") 
            ?: com.acj.firma.lib.LibUtilitario.extractIssuerField(this, "2.5.4.3") ?: ""
        val nonRepudiation = try {
            keyUsage?.let { it.size > 1 && it[1] } ?: false
        } catch (_: Exception) {
            false
        }

        return Certificado(
            alias = alias,
            nombreComun = cn,
            organizacion = org,
            emisor = issuerCn,
            validoDesde = notBefore,
            validoHasta = notAfter,
            tieneNoRepudio = nonRepudiation,
            serial = serialNumber.toString(16).uppercase(),
        )
    }

    /**
     * Convierte los parámetros de firma de dominio hacia la clase Parameters requerida por la biblioteca nativa.
     * Configura rutas, credenciales y opciones visuales de la firma.
     *
     * @param context Contexto necesario para el controlador nativo.
     * @return Objeto Parameters configurado para la ejecución.
     */
    fun DocumentoFirma.toParameters(context: Context): Parameters {
        return Parameters().apply {
            setContext(context)

            setRutaPdfOriginal(this@toParameters.archivo.absolutePath)
            setRutaDestino(this@toParameters.rutaDestino)
            setSufijo(this@toParameters.sufijo)

            setAliasCertificado(this@toParameters.aliasCertificado)
            setMotivo(this@toParameters.motivo)
            setLocation(this@toParameters.lugar)

            setLevel(this@toParameters.nivel.name)

            setVisibleFirma(this@toParameters.firmaVisible != null)

            this@toParameters.firmaVisible?.let { fv ->
                setPagina(fv.pagina)
                setX(fv.x)
                setY(fv.y)
                setWidth(fv.ancho)
                setHeight(fv.alto)
                setFontSize(fv.fontSize)
                setTituloFirma(fv.titulo)
                setRutaImagen(fv.rutaImagen)
                setAppearance(fv.apariencia)
                setIncluirCargo(fv.incluirCargo)
                setIncluirEmpresa(fv.incluirEmpresa)
            }

            setVerificarTsl(this@toParameters.tsl.verificar)
            setTslUrl(this@toParameters.tsl.url)

            setVerificarTsa(this@toParameters.tsa.verificar)
            setTsaUrl(this@toParameters.tsa.url)
        }
    }

    /**
     * Transforma el resultado de validación nativo de la biblioteca hacia el modelo consolidado de dominio.
     *
     * @param certs Mapa de certificados extraídos del PDF para enriquecer el detalle.
     * @return Objeto ResultadoValidacion con el informe completo.
     */
    fun ValidacionController.ResultadoValidacion.toResultadoValidacion(certs: Map<String, X509Certificate>): ResultadoValidacion {
        return ResultadoValidacion(
            esValido = this.documentoValido,
            firmas = firmas.mapIndexed { index, firma -> 
                val cert = certs.values.elementAtOrNull(index) // Asumiendo que el orden coincide o se usa el nombre de firma
                firma.toDomain(cert) 
            },
            mensajeGeneral = this.mensajeError
        )
    }

    /**
     * Mapea el detalle de una firma individual desde la biblioteca hacia el modelo de dominio extendido.
     * Realiza extracción exhaustiva de OIDs (DNI, Cargo, Empresa, etc.) desde el certificado embebido.
     *
     * @param cert Certificado X.509 asociado a la firma analizada.
     * @return Objeto ResultadoValidacionFirma con metadatos técnicos y del titular.
     */
    fun ValidacionController.ResultadoFirma.toDomain(cert: X509Certificate?): ResultadoValidacionFirma {
        val subjectNameStr = cert?.subjectX500Principal?.name

        return ResultadoValidacionFirma(
            esValida = this.valida,
            firmante = this.nombreFirmante,
            motivo = this.motivo,
            location = this.location,
            fechaFirma = this.fechaFirma,
            mensajeError = this.mensajeError,
            subjectDn = subjectNameStr,
            dniRuc = cert?.let { com.acj.firma.lib.LibUtilitario.extractField(it, "2.5.4.5") } 
                ?: cert?.let { com.acj.firma.lib.LibUtilitario.extractField(it, "2.5.4.97") },
            email = cert?.let { com.acj.firma.lib.LibUtilitario.extractField(it, "1.2.840.113549.1.9.1") },
            cargo = cert?.let { com.acj.firma.lib.LibUtilitario.extractField(it, "2.5.4.12") },
            empresa = cert?.let { com.acj.firma.lib.LibUtilitario.extractField(it, "2.5.4.10") },
            unidad = cert?.let { 
                com.acj.firma.lib.LibUtilitario.extractAllFields(it, "2.5.4.11")
                    .filterNot { ou -> ou.all { c -> c.isDigit() } || ou.startsWith("RUC", ignoreCase = true) }
                    .joinToString(" - ")
            }?.takeIf { it.isNotBlank() },
            emisorCertificado = cert?.let { com.acj.firma.lib.LibUtilitario.extractIssuerField(it, "2.5.4.10") 
                ?: com.acj.firma.lib.LibUtilitario.extractIssuerField(it, "2.5.4.3") },
            formatoCertificado = if (this.selloMarcaDeHora != null) "PKCS7-T" else "PKCS7-B",
            serialCertificado = cert?.serialNumber?.toString(),
            fechaEmision = cert?.notBefore,
            fechaExpiracion = cert?.notAfter,
            selloEmitidoPor = this.selloEmitidoPor,
            selloMarcaDeHora = this.selloMarcaDeHora,
            selloValidoHasta = this.selloValidoHasta
        )
    }
}