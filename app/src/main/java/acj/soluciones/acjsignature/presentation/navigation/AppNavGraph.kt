package acj.soluciones.acjsignature.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import acj.soluciones.acjsignature.presentation.certificados.CertificadosScreen
import acj.soluciones.acjsignature.presentation.configuracion.ConfiguracionScreen
import acj.soluciones.acjsignature.presentation.firma.FirmaScreen
import acj.soluciones.acjsignature.presentation.firma.PosicionarFirmaScreen
import acj.soluciones.acjsignature.presentation.home.HomeScreen
import acj.soluciones.acjsignature.presentation.validacion.ValidacionScreen
import acj.soluciones.acjsignature.presentation.ajustes.AjustesScreen
import acj.soluciones.acjsignature.presentation.ajustes.AcercaDeScreen
import acj.soluciones.acjsignature.presentation.logs.LogsAuditoriaScreen
import acj.soluciones.acjsignature.presentation.tsl.TSLScreen
import acj.soluciones.acjsignature.presentation.login.LoginScreen
import acj.soluciones.acjsignature.presentation.register.RegisterScreen
import acj.soluciones.acjsignature.presentation.main.MainViewModel
import acj.soluciones.acjsignature.shared.ui.components.ACJBottomNavBar

/**
 * Orquestador principal de la navegación en la aplicación.
 * Define el grafo de navegación, asocia rutas con pantallas y gestiona la visibilidad
 * de la barra de navegación inferior según el destino actual.
 *
 * @param navController Controlador de navegación de Jetpack Compose.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val isLoggedIn by mainViewModel.isLoggedIn.collectAsState(initial = false)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Routes that show the bottom nav bar
    val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }

    androidx.compose.runtime.LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn && currentRoute != Screen.Register.route && currentRoute != Screen.Login.route) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        bottomBar = {
            if (showBottomBar) {
                ACJBottomNavBar(
                    currentRoute = currentRoute,
                    onItemSelected = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route,
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp
            ),
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToFirmar = { navController.navigate(Screen.SubirPdf.route) },
                    onNavigateToCertificados = { navController.navigate(Screen.Certificados.route) },
                    onNavigateToDocumentos = {
                        navController.navigate(Screen.DocumentosFirmados.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToAjustes = {
                        navController.navigate(Screen.Ajustes.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToValidar = { navController.navigate(Screen.ValidarPdf.route) }
                )
            }

            composable(Screen.SubirPdf.route) {
                FirmaScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToPosicionar = { docId ->
                        navController.navigate(Screen.PosicionarFirma.createRoute(docId))
                    },
                )
            }

            composable(
                route = Screen.PosicionarFirma.route,
                arguments = listOf(navArgument("docId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val docId = backStackEntry.arguments?.getLong("docId") ?: 0L
                PosicionarFirmaScreen(
                    docId = docId,
                    onBack = { navController.popBackStack() },
                    onConfirm = {
                        navController.navigate(Screen.DocumentosFirmados.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        }
                    },
                )
            }

            composable(Screen.DocumentosFirmados.route) {
                ValidacionScreen(
                    onNavigateToFirmar = { navController.navigate(Screen.SubirPdf.route) },
                )
            }

            composable(Screen.Certificados.route) {
                CertificadosScreen()
            }

            composable(Screen.Ajustes.route) {
                AjustesScreen(
                    onNavigateToAcercaDe = { navController.navigate(Screen.AcercaDe.route) },
                    onNavigateToConfiguracionFirma = { navController.navigate(Screen.ConfiguracionFirma.route) },
                    onNavigateToTSL = { navController.navigate(Screen.TslConfig.route) },
                    onNavigateToLogs = { navController.navigate(Screen.LogsAuditoria.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.ConfiguracionFirma.route) {
                ConfiguracionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.TslConfig.route) {
                TSLScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.LogsAuditoria.route) {
                LogsAuditoriaScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AcercaDe.route) {
                AcercaDeScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            navigation(
                startDestination = Screen.ValidarPdf.route,
                route = "validar_flow"
            ) {
                composable(Screen.ValidarPdf.route) { backStackEntry ->
                    val parentEntry = androidx.compose.runtime.remember(backStackEntry) {
                        navController.getBackStackEntry("validar_flow")
                    }
                    val viewModel = androidx.hilt.navigation.compose.hiltViewModel<acj.soluciones.acjsignature.presentation.validarpdf.ValidarPdfViewModel>(parentEntry)
                    acj.soluciones.acjsignature.presentation.validarpdf.ValidarPdfScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToResultados = { navController.navigate(Screen.ResultadoValidacion.route) },
                        viewModel = viewModel
                    )
                }
                
                composable(Screen.ResultadoValidacion.route) { backStackEntry ->
                    val parentEntry = androidx.compose.runtime.remember(backStackEntry) {
                        navController.getBackStackEntry("validar_flow")
                    }
                    val viewModel = androidx.hilt.navigation.compose.hiltViewModel<acj.soluciones.acjsignature.presentation.validarpdf.ValidarPdfViewModel>(parentEntry)
                    acj.soluciones.acjsignature.presentation.validarpdf.ResultadoValidacionScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToDetalle = { index -> 
                            navController.navigate(Screen.DetalleFirmante.createRoute(index))
                        },
                        viewModel = viewModel
                    )
                }

                composable(
                    route = Screen.DetalleFirmante.route,
                    arguments = listOf(androidx.navigation.navArgument("index") { type = androidx.navigation.NavType.IntType })
                ) { backStackEntry ->
                    val parentEntry = androidx.compose.runtime.remember(backStackEntry) {
                        navController.getBackStackEntry("validar_flow")
                    }
                    val viewModel = androidx.hilt.navigation.compose.hiltViewModel<acj.soluciones.acjsignature.presentation.validarpdf.ValidarPdfViewModel>(parentEntry)
                    val index = backStackEntry.arguments?.getInt("index") ?: 0
                    acj.soluciones.acjsignature.presentation.validarpdf.DetalleFirmanteScreen(
                        firmaIndex = index,
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}