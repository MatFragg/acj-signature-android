# ACJ Firma Android - Documentacion Tecnica de la Libreria

## Tabla de contenidos

1. Objetivo y alcance
2. Vista general de la libreria
3. Dependencias y configuracion de build
4. Estructura del modulo
5. API publica y componentes principales
6. Flujo de firma
7. Flujo de validacion
8. Componentes TSL y confianza
9. Utilidades y configuracion
10. Archivos y clases clave (resumen)
11. Funciones principales por archivo
12. Ejemplo de uso (integracion)
13. Consideraciones operativas y limites
14. Checklist de verificacion tecnica

---

## 1) Objetivo y alcance

Este documento resume, describe e ilustra la libreria `acjfirmalib` del proyecto `acj-firma-android`.

Incluye:

- Dependencias y configuracion del modulo Android Library.
- Estructura de paquetes y archivos de codigo principales.
- Descripcion funcional de controladores y librerias auxiliares.
- Flujo de firma y flujo de validacion.
- Componentes de TSL (Trusted Service List) y utilidades de soporte.

No incluye detalles de UI (la libreria esta orientada a logica de firma/validacion).

---

## 2) Vista general de la libreria

- Modulo: `acjfirmalib`
- Namespace: `com.acj.firma`
- Tipo: Android Library (AAR)
- SDK:
    - `minSdk = 26`
    - `targetSdk = 34`
    - `compileSdk = 34`
- Java: `17`

La libreria concentra tres capas:

- `controller/`: orquestacion de casos de uso de firma y validacion.
- `lib/`: servicios tecnicos de TSL, XML/XSLT y utilitarios internos.
- `util/`: modelos de parametros, constantes, helpers y funciones comunes.

---

## 3) Dependencias y configuracion de build

Fuente: `acjfirmalib/build.gradle.kts`

### Dependencias declaradas

- PDF:
    - `com.tom-roush:pdfbox-android:2.0.27.0`
- Criptografia:
    - `org.bouncycastle:bcpkix-jdk15to18:1.78.1`
    - `org.bouncycastle:bcprov-jdk15to18:1.78.1`
    - `org.bouncycastle:bctls-jdk15to18:1.78.1`
- XML/Red:
    - `org.jdom:jdom2:2.0.6`
    - `com.squareup.okhttp3:okhttp:4.12.0`
- Utilitarios:
    - `commons-io:commons-io:2.15.1`
    - `commons-codec:commons-codec:1.16.1`
- Testing:
    - `junit:junit:4.13.2`
    - `androidx.test.ext:junit:1.1.5`
    - `androidx.test:runner:1.5.2`
    - `androidx.test:core:1.5.0`
    - `androidx.test:monitor:1.6.1`

### Configuracion relevante

- `packaging.resources.excludes` para evitar conflictos `META-INF`.
- `testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"`.

---

## 4) Estructura del modulo

Ruta base: `acjfirmalib/src/main/java/com/acj/firma`

- `controller/`
    - `FirmaController.java`
    - `ValidacionController.java`
- `lib/`
    - `LibCargarTsl.java`
    - `LibDssXmlUtilitario.java`
    - `LibTslRepositorio.java`
    - `LibTslValidador.java`
    - `LibUtilitario.java`
    - `LibXSLTServicio.java`
- `util/`
    - `Common.java`
    - `Constants.java`
    - `Parameters.java`
    - `PropertiesSingleton.java`
    - `Tsl.java`
    - `UtilAndroid.java`
    - `UtilSign.java`

---

## 5) API publica y componentes principales

### `controller/FirmaController.java`

Responsabilidad principal:

- Orquestar la firma digital del documento desde los parametros de entrada.
- Coordinar carga de recursos criptograficos y salida firmada.

Funciones clave (nivel funcional):

- Preparacion de insumos de firma (documento, credenciales, configuracion).
- Ejecucion del proceso criptografico y ensamblado de resultado.
- Manejo de respuestas/errores para el consumidor de la libreria.

### `controller/ValidacionController.java`

Responsabilidad principal:

- Orquestar la validacion de firmas y construccion del resultado de validacion.

Funciones clave (nivel funcional):

- Analizar firma y contexto de confianza.
- Integrar TSL/politicas cuando corresponde.
- Generar salidas de validacion (estado y detalles).

### `util/Parameters.java`

Responsabilidad principal:

- Centralizar los parametros de entrada/salida requeridos en operaciones de firma y validacion.

Uso:

- Objeto de transporte de datos para controllers y servicios.

---

## 6) Flujo de firma

Flujo conceptual (alto nivel):

1. El consumidor construye `Parameters` con documento, metadatos y configuracion.
2. `FirmaController` valida precondiciones y prepara contexto.
3. Se aplican utilitarios criptograficos y de documento (`UtilSign`, `LibUtilitario`).
4. Se genera el artefacto firmado.
5. Se retorna resultado con estado y datos de salida.

Representacion simplificada:

```text
App Cliente -> Parameters -> FirmaController
FirmaController -> UtilSign / LibUtilitario -> Documento firmado
Documento firmado -> App Cliente
```

---

## 7) Flujo de validacion

Flujo conceptual (alto nivel):

1. El consumidor envia documento firmado y parametros de validacion.
2. `ValidacionController` prepara evaluacion.
3. Se consulta/usa contexto de confianza (TSL/politicas) via capa `lib`.
4. Se procesa el resultado de validacion.
5. Se retorna estado de validez y detalle.

Representacion simplificada:

```text
App Cliente -> Parameters -> ValidacionController
ValidacionController -> LibTslRepositorio / LibTslValidador
ValidacionController -> LibDssXmlUtilitario / LibXSLTServicio
Resultado validacion -> App Cliente
```

---

## 8) Componentes TSL y confianza

### `lib/LibCargarTsl.java`

- Carga y preparacion de TSL para su uso en validacion.

### `lib/LibTslRepositorio.java`

- Gestion de repositorio/fuentes TSL (obtencion, persistencia o actualizacion segun flujo implementado).

### `lib/LibTslValidador.java`

- Validacion del material TSL y aplicacion al contexto de confianza.

### `util/Tsl.java`

- Modelo/soporte utilitario relacionado a TSL y su manejo interno.

---

## 9) Utilidades y configuracion

### `lib/LibDssXmlUtilitario.java`

- Funciones XML de soporte para lectura/transformacion de resultados.

### `lib/LibXSLTServicio.java`

- Procesamiento XSLT para reportes o salidas transformadas.

### `lib/LibUtilitario.java`

- Utilidades generales compartidas por firma y validacion.

### `util/Common.java`

- Utilidades comunmente reutilizadas en la libreria.

### `util/Constants.java`

- Constantes centrales (claves, rutas logicas, codigos, etc.).

### `util/PropertiesSingleton.java`

- Acceso centralizado a propiedades de configuracion.

### `util/UtilAndroid.java`

- Funciones de apoyo especificas para entorno Android.

### `util/UtilSign.java`

- Operaciones utilitarias asociadas al proceso de firma.

---

## 10) Archivos y clases clave (resumen)

| Ruta | Rol principal |
|---|---|
| `acjfirmalib/src/main/java/com/acj/firma/controller/FirmaController.java` | Orquestacion de firma |
| `acjfirmalib/src/main/java/com/acj/firma/controller/ValidacionController.java` | Orquestacion de validacion |
| `acjfirmalib/src/main/java/com/acj/firma/util/Parameters.java` | Parametros de operacion |
| `acjfirmalib/src/main/java/com/acj/firma/lib/LibTslRepositorio.java` | Gestion TSL |
| `acjfirmalib/src/main/java/com/acj/firma/lib/LibTslValidador.java` | Validacion TSL |
| `acjfirmalib/src/main/java/com/acj/firma/lib/LibXSLTServicio.java` | Transformaciones XSLT |
| `acjfirmalib/src/main/java/com/acj/firma/lib/LibDssXmlUtilitario.java` | Utilidades XML |
| `acjfirmalib/src/main/java/com/acj/firma/util/UtilSign.java` | Helpers de firma |

---

## 11) Funciones principales por archivo

### `acjfirmalib/src/main/java/com/acj/firma/controller/FirmaController.java`

- `firmarDocumento(Parameters)`: flujo principal de firma PDF (carga clave/certificado, configura firma PAdES y guarda el resultado incremental).
- `generarImagenFirmaBytes(X509Certificate, Parameters)`: construye la imagen PNG de firma visible (simple o con logo).
- `buildSignatureInterface(PrivateKey, X509Certificate, X509Certificate[])`: crea la firma CMS/PKCS#7 detached para PDFBox usando la cadena de certificados.
- `construirNombreSalida(String, String)`: genera el nombre de salida con sufijo.
- `leerStream(InputStream)`: utilitario para leer bytes completos de un stream.

### `acjfirmalib/src/main/java/com/acj/firma/controller/ValidacionController.java`

- `validarDocumento(Context, File, Tsl)`: valida todas las firmas de un PDF desde archivo.
- `validarDocumento(Context, InputStream, Tsl)`: valida todas las firmas de un PDF desde stream en memoria.
- `validarFirma(PDDocument, PDSignature, Tsl, byte[])`: valida criptografia CMS, anclaje TSL, vigencia y usos de certificado por firma.
- `verificarCadenaConCrl(X509Certificate, List<X509Certificate>)`: verifica estado de revocacion por CRL para la cadena de confianza.
- `cadenaAnclaTsl(X509Certificate, List<X509Certificate>, Tsl)`: comprueba que la cadena del firmante ancle contra alguna CA de la TSL.
- `obtenerBytesFirmados(byte[], PDSignature)`: extrae los bytes firmados del `ByteRange`.

### `acjfirmalib/src/main/java/com/acj/firma/lib/LibCargarTsl.java`

- `getInstance(Context, String, String, Boolean)`: obtiene/crea el singleton de carga TSL.
- `cargarTsl(Long)`: carga certificados TSL desde URL principal y, si falla, URL alternativa.
- `isValida()`: indica si la TSL quedo cargada sin error.
- `verifyTsl()`: dispara verificacion de vigencia via repositorio.

### `acjfirmalib/src/main/java/com/acj/firma/lib/LibDssXmlUtilitario.java`

- `buildDOM()`: crea un `Document` vacio seguro.
- `buildDOM(String)`: parsea XML en texto a `Document`.
- `buildDOM(byte[])`: parsea XML en bytes a `Document`.
- `buildDOM(InputStream)`: parsea XML desde stream a `Document`.
- `crearBuilder()`: configura `DocumentBuilderFactory` con proteccion XXE.

### `acjfirmalib/src/main/java/com/acj/firma/lib/LibTslRepositorio.java`

- `load(String, String, Long, String, Boolean)`: descarga/lee cache, parsea, filtra y valida vigencia de TSL.
- `verifyTslExpiration(String)`: verifica fechas de vigencia de la TSL por pais.
- `getByCountry(String)`: retorna certificados TSL cargados para un pais.
- `obtenerBytes(String, String, Long)`: decide cache vs descarga remota.
- `parsear(byte[], String)`: parsea XML TSL y extrae fechas/certificados.
- `filtrar(List<X509Certificate>, String)`: aplica regex sobre DN emisor.

### `acjfirmalib/src/main/java/com/acj/firma/lib/LibTslValidador.java`

- `validar()`: valida la firma CMS de la TSL contra certificados firmantes potenciales.

### `acjfirmalib/src/main/java/com/acj/firma/lib/LibUtilitario.java`

- `getSubjectCN/getSubjectDN/getIssuerCN/getIssuerDN(...)`: extrae datos DN de certificados.
- `verifyOfflineWithoutGuiInfo(X509Certificate)`: validacion offline basica de vigencia.
- `sha256InBytes(String)` y `sha1InHex(...)`: hashing para texto/bytes/archivo.
- `isTslAddressValid(String)` e `isUrlValid(String)`: validacion de URLs.
- `getComputadorId(Context)`: genera identificador de dispositivo Android.
- `LOG(String, String, String)`: logging centralizado de libreria/componente.

### `acjfirmalib/src/main/java/com/acj/firma/lib/LibXSLTServicio.java`

- `init(String)`: inicializa transformadores XSLT (modo local o web).
- `generarSimpleReport(String)`: genera reporte simple local.
- `generarSimpleReportWeb(String)`: genera reporte simple web.
- `generarDetailedReport(String)`: genera reporte detallado.
- `transformar(Transformer, String)`: aplica transformacion XSLT a XML.

### `acjfirmalib/src/main/java/com/acj/firma/util/Common.java`

- `getInstance(Context)`: singleton de propiedades `common.properties`.
- `getTslUrl()/getTslAlternativeUrl()`: obtiene URLs TSL.
- `getTsaUrl()/getTsaAlternativeUrl()`: obtiene URLs TSA.
- `getTsaUser()/getTsaPassword()`: obtiene credenciales TSA.

### `acjfirmalib/src/main/java/com/acj/firma/util/Constants.java`

- Define constantes globales de la libreria (formatos, tipos de firma, apariencia, extras y tipos de log).

### `acjfirmalib/src/main/java/com/acj/firma/util/Parameters.java`

- DTO de configuracion de firma/validacion con getters/setters para rutas, datos de firma visible, TSL/TSA y contexto.

### `acjfirmalib/src/main/java/com/acj/firma/util/PropertiesSingleton.java`

- `getInstancia(Context)`: singleton de `acjfirma-lib.properties`.
- `getPropiedad(String)`: lectura de propiedad con valor por defecto.

### `acjfirmalib/src/main/java/com/acj/firma/util/Tsl.java`

- `Tsl(String, Long, boolean, Context)`: carga TSL (cache/descarga), parsea certificados y valida vigencia opcional.
- `getCertificados()/isValida()/getMessageError_()`: expone estado y datos de carga.
- `parsearTsl(byte[])`: extrae fechas y certificados desde XML TSL.
- `verificarVigencia()`: comprueba ventana de vigencia de la TSL.

### `acjfirmalib/src/main/java/com/acj/firma/util/UtilAndroid.java`

- `getListCertificados()`: lista alias aptos para firma (bit no-repudio).
- `getPrivateKey(String)` y `getCertificate(String)`: acceso a clave/certificado en AndroidKeyStore.
- `getCertificateChain(String)`: retorna la cadena de certificados del alias.
- `importarP12(byte[], char[], String)`: importa PKCS#12 al AndroidKeyStore.
- `existeAlias(String)`: valida existencia de alias en keystore.

### `acjfirmalib/src/main/java/com/acj/firma/util/UtilSign.java`

- `getCertificadoInfo(X509Certificate, String)`: extrae atributos del subject por OID (CN, O, OU, etc.).
- `nonRepudiation(X509Certificate)`: valida bit de no-repudio.
- `formatDateFull(Date)`, `cropText(String, Integer)`, `retornarCadenaList(List<String>)`: utilidades de formato.
- `cargarTsl(Long, String, boolean, Context)`: helper para instanciar/cargar TSL.
- `generarImagenFirmaSimple(...)` y `generarImagenFirmaConLogo(...)`: genera bitmaps de firma visible.

---

## 12) Ejemplo de uso (integracion)

Ejemplo referencial (pseudocodigo Java):

```java
Parameters params = new Parameters();
// completar campos requeridos (documento, configuracion, etc.)

FirmaController firmaController = new FirmaController();
Object resultadoFirma = firmaController; // invocar metodo de firma segun API concreta

ValidacionController validacionController = new ValidacionController();
Object resultadoValidacion = validacionController; // invocar metodo de validacion segun API concreta
```

Nota: ajustar nombres de metodos concretos segun firmas reales de cada clase.

---

## 13) Consideraciones operativas y limites

- La robustez del flujo de validacion depende de la disponibilidad y vigencia del material TSL.
- Las capacidades exactas de firma (formatos y variantes) dependen de la implementacion de `FirmaController` y `UtilSign`.
- El detalle del reporte de validacion depende de las transformaciones XML/XSLT configuradas.
- Se recomienda versionar y auditar cambios en constantes y propiedades de configuracion.

---

## 14) Checklist de verificacion tecnica

- [x] Se identifico el modulo y su configuracion de build.
- [x] Se listaron dependencias principales de ejecucion y testing.
- [x] Se documento la estructura por paquetes (`controller`, `lib`, `util`).
- [x] Se describieron componentes de firma y validacion.
- [x] Se incluyo flujo de confianza TSL.
- [x] Se incluyo mapa de archivos clave y su rol.

---

## Anexo A: Referencias rapidas

- Build del modulo: `acjfirmalib/build.gradle.kts`
- Documentacion: `docs/document.md`
- Controladores:
    - `acjfirmalib/src/main/java/com/acj/firma/controller/FirmaController.java`
    - `acjfirmalib/src/main/java/com/acj/firma/controller/ValidacionController.java`
- Librerias internas:
    - `acjfirmalib/src/main/java/com/acj/firma/lib/LibCargarTsl.java`
    - `acjfirmalib/src/main/java/com/acj/firma/lib/LibDssXmlUtilitario.java`
    - `acjfirmalib/src/main/java/com/acj/firma/lib/LibTslRepositorio.java`
    - `acjfirmalib/src/main/java/com/acj/firma/lib/LibTslValidador.java`
    - `acjfirmalib/src/main/java/com/acj/firma/lib/LibUtilitario.java`
    - `acjfirmalib/src/main/java/com/acj/firma/lib/LibXSLTServicio.java`
- Utilidades:
    - `acjfirmalib/src/main/java/com/acj/firma/util/Common.java`
    - `acjfirmalib/src/main/java/com/acj/firma/util/Constants.java`
    - `acjfirmalib/src/main/java/com/acj/firma/util/Parameters.java`
    - `acjfirmalib/src/main/java/com/acj/firma/util/PropertiesSingleton.java`
    - `acjfirmalib/src/main/java/com/acj/firma/util/Tsl.java`
    - `acjfirmalib/src/main/java/com/acj/firma/util/UtilAndroid.java`
    - `acjfirmalib/src/main/java/com/acj/firma/util/UtilSign.java`

