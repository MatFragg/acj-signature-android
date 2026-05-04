package acj.soluciones.acjsignature.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Routes that show the bottom nav bar
    val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                ACJBottomNavBar(
                    currentRoute = currentRoute,
                    onItemSelected = { screen ->
                        navController.navigate(screen.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
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
                    onNavigateToConfiguracion = {
                        navController.navigate(Screen.Configuracion.route) {
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

            composable(Screen.Configuracion.route) {
                ConfiguracionScreen()
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