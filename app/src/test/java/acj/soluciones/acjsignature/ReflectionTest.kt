package acj.soluciones.acjsignature

import com.acj.firma.controller.ValidacionController
import org.junit.Test

class ReflectionTest {
    @Test
    fun testPrintFields() {
        val methods = ValidacionController.ResultadoFirma::class.java.methods
        methods.forEach { println("METHOD: ${it.name} -> ${it.returnType.name}") }
        
        val fields = ValidacionController.ResultadoFirma::class.java.declaredFields
        fields.forEach { println("FIELD: ${it.name} -> ${it.type.name}") }
    }
}
