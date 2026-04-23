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

object FirmaMapper {

    fun X509Certificate.toCertificado(alias: String): Certificado {
        val subject = subjectX500Principal.name
        val issuer = issuerX500Principal.name
        val cn = extractField(subject, "CN") ?: alias
        val org = extractField(subject, "O") ?: ""
        val issuerCn = extractField(issuer, "CN") ?: extractField(issuer, "O") ?: ""
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

    private fun extractField(dn: String, field: String): String? {
        return dn.split(",")
            .map { it.trim() }
            .firstOrNull { it.startsWith("$field=", ignoreCase = true) }
            ?.substringAfter("=")
    }

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

    fun ValidacionController.ResultadoValidacion.toResultadoValidacion(): ResultadoValidacion {
        return ResultadoValidacion(
            esValido = this.documentoValido,
            firmas = firmas.map { it.toDomain() },
            mensajeGeneral = this.mensajeError
        )
    }

    fun ValidacionController.ResultadoFirma.toDomain(): ResultadoValidacionFirma {
        return ResultadoValidacionFirma(
            esValida = this.valida,
            firmante = this.nombreFirmante,
            motivo = this.motivo,
            location = this.location,
            fechaFirma = this.fechaFirma,
            mensajeError = this.mensajeError
        )
    }

}