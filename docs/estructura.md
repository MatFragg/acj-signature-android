app/
├── build.gradle ← implementation(project(':acjfirmalib'))
└── src/main/
├── AndroidManifest.xml
├── assets/reports/ ← xmls y xslts de la librería
└── java/com/tuempresa/firmapp/
├── presentation/
│ ├── firma/
│ │ ├── FirmaScreen.kt
│ │ ├── FirmaViewModel.kt
│ │ └── FirmaState.kt
│ ├── validacion/
│ │ ├── ValidacionScreen.kt
│ │ ├── ValidacionViewModel.kt
│ │ └── ValidacionState.kt
│ ├── certificados/
│ │ ├── CertificadosScreen.kt
│ │ └── CertificadosViewModel.kt
│ └── navigation/
│ ├── AppNavGraph.kt
│ └── Screen.kt
├── domain/
│ ├── model/
│ │ ├── DocumentoFirma.kt
│ │ ├── ResultadoFirma.kt
│ │ ├── Certificado.kt
│ │ └── ResultadoValidacion.kt
│ ├── repository/
│ │ └── FirmaRepository.kt ← interfaz, sin imports de AAR
│ └── usecase/
│ ├── FirmarDocumentoUseCase.kt
│ ├── ListarCertificadosUseCase.kt
│ └── ValidarDocumentoUseCase.kt
├── data/
│ ├── firma/
│ │ ├── FirmaRepositoryImpl.kt ← única clase que toca acjfirmalib
│ │ ├── mapper/
│ │ │ └── FirmaMapper.kt
│ │ └── remote/ ← vacío hoy, listo para API
│ │ └── FirmaApiService.kt (futuro)
│ └── di/
│ ├── AppModule.kt
│ └── FirmaModule.kt
└── shared/
├── domain/
│ └── Result.kt
└── ui/
├── components/
└── theme/

acjfirmalib/ ← módulo AAR (tu librería)
